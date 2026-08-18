package com.anmol.distrilimit;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AdaptiveLimitService {

    private final ChatClient chatClient;

    public AdaptiveLimitService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public RateLimitDecision decide(String clientId, int requestRatePerMinute, double throttleRatePercent, int currentCapacity){
        String prompt= """
                You are helping manage rate limits for an API client.
                
                Client ID: %s
                Current configured capacity: %d requests
                Recent request rate: %d requests per minute
                Recent throttle rate: %.1f%% of requests were rejected
                
                Decide whether to tighten, relax, or leave unchanged the rate limit capacity for this client.
                If throttle rate is high and request rate looks abusive, tighten.
                If throttle rate is low and traffic looks like a legitimate burst, relax.
                Otherwise, no change.
                
                Respond with a decision.
                """.formatted(clientId, currentCapacity, requestRatePerMinute, throttleRatePercent);

        return chatClient.prompt()
                .user(prompt).
                call()
                .entity(RateLimitDecision.class);

    }
}
