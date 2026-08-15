package com.anmol.distrilimit;

public class TokenBucket {
    private final int capacity;
    private final double refillRatePerSecond;

    private double currentTokens;
    private long lastRefillTimeStamp;

    public TokenBucket(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.currentTokens = capacity;
        this.lastRefillTimeStamp = System.nanoTime();
    }

    public synchronized boolean tryConsume(){
        refill();

        if(currentTokens >= 1){
            currentTokens--;
            return true;
        }
        return false;
    }

    public void refill(){
        long now = System.nanoTime();
        double secondsEscaped=(now-lastRefillTimeStamp)/1000000000.0;

        double tokensToAdd=secondsEscaped*refillRatePerSecond;

        currentTokens=Math.min(capacity,currentTokens+tokensToAdd);
        lastRefillTimeStamp=now;
    }
}
