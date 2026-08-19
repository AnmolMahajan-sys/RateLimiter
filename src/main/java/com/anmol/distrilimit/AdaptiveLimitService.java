package com.anmol.distrilimit;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AdaptiveLimitService {

    private final ChatClient chatClient;

    public AdaptiveLimitService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public RateLimitDecision decideWithPromptConstraint(String clientId, int requestRatePerMinute, double throttleRatePercent, int currentCapacity) {
        String prompt = """
                You are helping manage rate limits for an API client.

                Client ID: %s
                Current configured capacity: %d requests
                Recent request rate: %d requests per minute
                Recent throttle rate: %.1f%% of requests were rejected

                Decide whether to tighten, relax, or leave unchanged the rate limit capacity for this client.
                If throttle rate is high and request rate looks abusive, tighten.
                If throttle rate is low and traffic looks like a legitimate burst, relax.
                Otherwise, no change.

                IMPORTANT numeric rule:
                - If action is "tighten", newCapacity MUST be strictly less than %d.
                - If action is "relax", newCapacity MUST be strictly greater than %d.
                - If action is "no_change", newCapacity MUST equal %d exactly.

                Respond with a decision.
                """.formatted(clientId, currentCapacity, requestRatePerMinute, throttleRatePercent,
                currentCapacity, currentCapacity, currentCapacity);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(RateLimitDecision.class);
    }

    public RateLimitDecision decideWithCodeComputation(String clientId, int requestRatePerMinute, double throttleRatePercent, int currentCapacity) {
        String prompt = """
                You are helping manage rate limits for an API client.

                Client ID: %s
                Current configured capacity: %d requests
                Recent request rate: %d requests per minute
                Recent throttle rate: %.1f%% of requests were rejected

                Decide whether to tighten, relax, or leave unchanged the rate limit capacity for this client.
                If throttle rate is high and request rate looks abusive, tighten.
                If throttle rate is low and traffic looks like a legitimate burst, relax.
                Otherwise, no change.

                Respond with just the action and a short reason. Do not calculate a specific number.
                """.formatted(clientId, currentCapacity, requestRatePerMinute, throttleRatePercent);

        RateLimitDecision modelDecision = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(RateLimitDecision.class);

        int computedCapacity = switch (modelDecision.action().toLowerCase()) {
            case "tighten" -> (int) Math.max(1, currentCapacity * 0.7);
            case "relax" -> (int) Math.ceil(currentCapacity * 1.3);
            default -> currentCapacity;
        };

        return new RateLimitDecision(modelDecision.action(), computedCapacity, modelDecision.reason());
    }
}
