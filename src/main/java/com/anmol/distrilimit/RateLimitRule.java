package com.anmol.distrilimit;

import jakarta.persistence.*;

@Entity
public class RateLimitRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String algorithm;
    private int capacity;
    private double rate;

    public RateLimitRule(){}
    public RateLimitRule(String clientId, String algorithm, int capacity, double rate) {
        this.clientId = clientId;
        this.algorithm = algorithm;
        this.capacity = capacity;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public double getRate() {
        return rate;
    }
    public void setRate(double rate) {
        this.rate = rate;
    }

}
