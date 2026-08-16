package com.anmol.distrilimit;

public interface RateLimiter {
    boolean tryConsume(String clientId);
}
