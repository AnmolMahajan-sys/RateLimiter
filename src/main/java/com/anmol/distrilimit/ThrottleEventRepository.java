package com.anmol.distrilimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ThrottleEventRepository extends JpaRepository<ThrottleEvent, Long>{

    @Query("SELECT DISTINCT t.clientId FROM ThrottleEvent t WHERE t.timestamp >= :sinceMillis")
    List<String> findDistinctClientIdsSince(@Param("sinceMillis") long sinceMillis);

    @Query("SELECT COUNT(t) FROM ThrottleEvent t WHERE t.clientId = :clientId AND t.timestamp >= :sinceMillis")
    long countByClientIdSince(@Param("clientId") String clientId, @Param("sinceMillis") long sinceMillis);
}
