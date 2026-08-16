package com.anmol.distrilimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

public class LeakyBucket implements RateLimiter{

    private final StringRedisTemplate redisTemplate;
    private final int capacity;
    private final double leakRatePerSecond;

    private static final DefaultRedisScript<Long> SCRIPT=new DefaultRedisScript<>();

    static{
        SCRIPT.setLocation(new ClassPathResource("leaky_bucket.lua"));
        SCRIPT.setResultType(Long.class);
    }
    public LeakyBucket(StringRedisTemplate redisTemplate, int capacity, double leakRatePerSecond) {
        this.redisTemplate = redisTemplate;
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
    }

    @Override
    public boolean tryConsume(String clientId) {
        String queueKey="leaky:"+clientId+":queue";
        String timeStampKey="leaky:"+clientId+":timeStamp";
        long now=System.currentTimeMillis();

        List<String> keys= List.of(queueKey,timeStampKey);

        Long result=redisTemplate.execute(
                SCRIPT,
                keys,
                String.valueOf(capacity),
                String.valueOf(leakRatePerSecond),
                String.valueOf(now)
        );
        return result!=null && result==1L;
    }
}
