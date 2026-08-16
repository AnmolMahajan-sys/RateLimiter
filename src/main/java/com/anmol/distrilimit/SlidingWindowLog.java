package com.anmol.distrilimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

public class SlidingWindowLog implements RateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final int limit;
    private final long windowSizeMillis;

    private static final DefaultRedisScript<Long> SCRIPT=new DefaultRedisScript<>();

    static {
        SCRIPT.setLocation(new ClassPathResource("sliding_window_log.lua"));
        SCRIPT.setResultType(Long.class);
    }

    public SlidingWindowLog(StringRedisTemplate redisTemplate, int limit, long windowSizeMillis) {
        this.redisTemplate = redisTemplate;
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean tryConsume(String clientId) {
        String logKey="slidinglog:"+clientId+":log";
        long now = System.currentTimeMillis();

        List<String> keys= List.of(logKey);

        Long result = redisTemplate.execute(
                SCRIPT,
                keys,
                String.valueOf(limit),
                String.valueOf(windowSizeMillis),
                String.valueOf(now)
        );
        return result != null && result == 1L;
    }
}
