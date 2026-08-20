package com.anmol.distrilimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdaptiveLimitScheduler {

    private static final long LOOKBACK_MILLIS = 60_000;
    private static final int HIGH_THROTTLE_THRESHOLD = 5;

    @Autowired
    private ThrottleEventRepository throttleEventRepository;

    @Autowired
    private RateLimitRuleRepository ruleRepository;

    @Autowired
    private AdaptiveLimitService adaptiveLimitService;

    @Scheduled(fixedRate = 30_000)
    public void reviewClientLimits() {
        long since = System.currentTimeMillis() - LOOKBACK_MILLIS;

        List<String> activeClients = throttleEventRepository.findDistinctClientIdsSince(since);

        for (String clientId : activeClients) {
            long throttleCount = throttleEventRepository.countByClientIdSince(clientId, since);

            RateLimitRule rule = ruleRepository.findByClientId(clientId)
                    .orElseGet(() -> ruleRepository.findByClientId("default").orElse(null));

            if (rule == null) {
                continue;
            }

            double throttleRatePercent = Math.min(100.0, throttleCount * 2.0);

            RateLimitDecision decision = adaptiveLimitService.decideWithCodeComputation(
                    clientId,
                    (int) throttleCount,
                    throttleRatePercent,
                    rule.getCapacity()
            );

            if (!decision.action().equalsIgnoreCase("no_change")) {
                RateLimitRule updatedRule = new RateLimitRule(clientId, rule.getAlgorithm(), decision.newCapacity(), rule.getRate());
                ruleRepository.save(updatedRule);
                System.out.println("Adaptive limit update: client=" + clientId
                        + " action=" + decision.action()
                        + " newCapacity=" + decision.newCapacity()
                        + " reason=" + decision.reason());
            }
        }
    }
}
