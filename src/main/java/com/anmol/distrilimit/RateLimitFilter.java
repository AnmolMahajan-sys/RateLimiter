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
    private static final int CAPACITY=5;
    public static final double RATE =1.0;
    private static final long WINDOW_MILLIS=10000;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, ThrottleEvent>  kafkaTemplate;

    @Value("${ratelimiter.algorithm}")
    private String algorithmName;

    private RateLimiter rateLimiter;

    @Override
    public void init(FilterConfig filterConfig){
        switch(algorithmName){
            case "leaky-bucket" -> rateLimiter = new LeakyBucket(redisTemplate, CAPACITY, RATE);
            case "sliding-window-log" -> rateLimiter = new SlidingWindowLog(redisTemplate, CAPACITY, WINDOW_MILLIS);
            case "sliding-window-counter" -> rateLimiter = new SlidingWindowCounter(redisTemplate, CAPACITY, WINDOW_MILLIS);
            default -> rateLimiter = new RedisTokenBucket(redisTemplate, CAPACITY, RATE);
        }
    }

    @Override
    public void doFilter(ServletRequest request,ServletResponse response, FilterChain chain)
            throws IOException, ServletException{

        String clientId=request.getRemoteAddr();

        if(!rateLimiter.tryConsume(clientId)){
            HttpServletResponse httpResponse=(HttpServletResponse)response;
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many requests!");

            ThrottleEvent event = new ThrottleEvent(clientId, algorithmName, System.currentTimeMillis());
            kafkaTemplate.send("throttle-events", event);

            return;
        }
        chain.doFilter(request,response);
    }
}
