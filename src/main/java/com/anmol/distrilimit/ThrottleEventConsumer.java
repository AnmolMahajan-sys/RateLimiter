package com.anmol.distrilimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ThrottleEventConsumer {

    @Autowired
    private ThrottleEventRepository repository;

    @KafkaListener(topics="throttle-events",groupId = "distrilimit-audit-group")
    public void consume(ThrottleEvent throttleEvent) {
        repository.save(throttleEvent);
        System.out.println("Throttled: client="+throttleEvent.getClientId()+
                " algorithm="+throttleEvent.getAlgorithm()+
                " at="+throttleEvent.getTimestamp());
    }
}
