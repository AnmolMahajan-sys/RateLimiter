package com.anmol.distrilimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

public class RedisTokenBucket {
    private final StringRedisTemplate redisTemplate;
    private final String clientId;
    private final int capacity;
    private final double refillRatePerSecond;

    private static final DefaultRedisScript<Long> SCRIPT = new DefaultRedisScript<>();

    static {
        SCRIPT.setLocation(new ClassPathResource(("token_bucket.lua")));
        SCRIPT.setResultType(Long.class);
    }

    public RedisTokenBucket(StringRedisTemplate redisTemplate, String clientId, int capacity,double refillRatePerSecond) {
        this.redisTemplate = redisTemplate;
        this.clientId = clientId;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public boolean tryConsume(){
        String tokensKey="bucket:"+clientId+":tokens";
        String timestampsKey="bucket:"+clientId+":timestamps";
        long now=System.currentTimeMillis();

        List<String> keys=List.of(tokensKey,timestampsKey);

        Long result = redisTemplate.execute(
                SCRIPT,
                keys,
                String.valueOf(capacity),
                String.valueOf(refillRatePerSecond),
                String.valueOf(now)
        );

        return result!=null && result==1L;
    }
}
