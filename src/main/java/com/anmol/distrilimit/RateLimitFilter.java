package com.anmol.distrilimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import jakarta.servlet.*;

@Component
public class RateLimitFilter implements Filter {
    private static final int DEFAULT_CAPACITY = 5;
    public static final double DEFAULT_RATE = 1.0;
    private static final long WINDOW_MILLIS = 10000;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, ThrottleEvent> kafkaTemplate;

    @Autowired
    private RateLimitRuleRepository ruleRepository;

    @Value("${ratelimiter.algorithm}")
    private String defaultAlgorithmName;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String clientId = request.getRemoteAddr();

        RateLimitRule rule = ruleRepository.findByClientId(clientId)
                .orElseGet(() -> ruleRepository.findByClientId("default")
                        .orElse(new RateLimitRule("default", defaultAlgorithmName, DEFAULT_CAPACITY, DEFAULT_RATE)));

        RateLimiter rateLimiter = buildRateLimiter(rule);

        if (!rateLimiter.tryConsume(clientId)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests!");

            ThrottleEvent event = new ThrottleEvent(clientId, rule.getAlgorithm(), System.currentTimeMillis());
            kafkaTemplate.send("throttle-events", event);

            return;
        }
        chain.doFilter(request, response);
    }

    private RateLimiter buildRateLimiter(RateLimitRule rule) {
        return switch (rule.getAlgorithm()) {
            case "leaky-bucket" -> new LeakyBucket(redisTemplate, rule.getCapacity(), rule.getRate());
            case "sliding-window-log" -> new SlidingWindowLog(redisTemplate, rule.getCapacity(), WINDOW_MILLIS);
            case "sliding-window-counter" -> new SlidingWindowCounter(redisTemplate, rule.getCapacity(), WINDOW_MILLIS);
            default -> new RedisTokenBucket(redisTemplate, rule.getCapacity(), rule.getRate());
        };
    }
}
