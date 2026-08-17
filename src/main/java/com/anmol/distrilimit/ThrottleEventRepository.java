package com.anmol.distrilimit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ThrottleEventRepository extends JpaRepository<ThrottleEvent, Long>{
}
