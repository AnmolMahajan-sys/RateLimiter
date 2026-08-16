package com.anmol.distrilimit;

public class ThrottleEvent {
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
