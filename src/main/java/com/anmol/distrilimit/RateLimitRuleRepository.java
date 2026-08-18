package com.anmol.distrilimit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateLimitRuleRepository extends JpaRepository<RateLimitRule, Long> {
    Optional<RateLimitRule> findByClientId(String clientId);
}
