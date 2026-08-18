package com.anmol.distrilimit;

public record RateLimitDecision(
        String action,
        int newCapacity,
        String reason
) {
}
