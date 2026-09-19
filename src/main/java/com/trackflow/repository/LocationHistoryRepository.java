package com.trackflow.repository;

import com.trackflow.entity.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {

    List<LocationHistory> findByTrackingEventIdOrderByRecordedAtAsc(Long trackingEventId);
}