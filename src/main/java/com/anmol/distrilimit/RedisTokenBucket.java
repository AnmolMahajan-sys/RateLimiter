package com.anmol.distrilimit;

import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisTokenBucket {
    private final StringRedisTemplate redisTemplate;
    private final String clientId;
    private final int capacity;
    private final double refillRatePerSecond;

    public RedisTokenBucket(StringRedisTemplate redisTemplate, String clientId, int capacity,double refillRatePerSecond) {
        this.redisTemplate = redisTemplate;
        this.clientId = clientId;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public boolean tryConsume(){
        String tokensKey="bucket:"+clientId+":tokens";
        String timestampsKey="bucket:"+clientId+":timestamps";

        String tokenStr=redisTemplate.opsForValue().get(tokensKey);
        String timestampStr=redisTemplate.opsForValue().get(timestampsKey);

        double currentTokens=(tokenStr!=null)? Double.parseDouble(tokenStr):capacity;
        long lastRefill=(timestampStr!=null)? Long.parseLong(timestampStr):System.currentTimeMillis();

        long now=System.currentTimeMillis();
        double secondsElapsed=(now-lastRefill)/1000.0;
        currentTokens=Math.min(capacity,currentTokens+secondsElapsed*refillRatePerSecond);

        boolean allowed;
        if(currentTokens>=1){
            currentTokens-=1;
            allowed=true;
        }else{
            allowed=false;
        }

        redisTemplate.opsForValue().set(tokensKey,String.valueOf(currentTokens));
        redisTemplate.opsForValue().set(timestampsKey,String.valueOf(now));

        return allowed;
    }
}
