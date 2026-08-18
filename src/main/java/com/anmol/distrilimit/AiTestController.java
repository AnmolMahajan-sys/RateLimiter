package com.anmol.distrilimit;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiTestController {
    private final ChatClient  chatClient;
    private final AdaptiveLimitService adaptiveLimitService;

    public AiTestController(ChatClient.Builder chatClientBuilder, AdaptiveLimitService adaptiveLimitService) {
        this.chatClient = chatClientBuilder.build();
        this.adaptiveLimitService = adaptiveLimitService.;
    }

    @GetMapping("/ai-test")
    public String test(@RequestParam(defaultValue = "Say hello") String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @GetMapping("/ai-decision-test")
    public RateLimitDecision decisionTest(){
        return adaptiveLimitService("test-client",120,45,0,5);
    }
}
