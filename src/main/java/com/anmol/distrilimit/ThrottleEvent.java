package com.anmol.distrilimit;

import jakarta.persistence.Id;
import jakarta.persistence.*;

@Entity
public class ThrottleEvent {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String algorithm;
    private long timestamp;

    public ThrottleEvent(){
    }

    public ThrottleEvent(String clientId, String algorithm, long timestamp) {
        this.clientId = clientId;
        this.algorithm = algorithm;
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
