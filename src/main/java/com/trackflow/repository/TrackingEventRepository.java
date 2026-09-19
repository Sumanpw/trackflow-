package com.trackflow.repository;

import com.trackflow.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    List<TrackingEvent> findByTrackingIdOrderByEventTimeDesc(String trackingId);

    List<TrackingEvent> findByShipmentIdOrderByEventTimeDesc(Long shipmentId);

    List<TrackingEvent> findByTrackingIdAndStatusOrderByEventTimeDesc(String trackingId, String status);
}