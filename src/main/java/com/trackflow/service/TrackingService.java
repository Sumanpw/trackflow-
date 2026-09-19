package com.trackflow.service;

import com.trackflow.entity.DriverSession;
import com.trackflow.entity.LocationHistory;
import com.trackflow.entity.Shipment;
import com.trackflow.entity.TrackingEvent;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.DriverSessionRepository;
import com.trackflow.repository.LocationHistoryRepository;
import com.trackflow.repository.ShipmentRepository;
import com.trackflow.repository.TrackingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TrackingService {

    @Autowired
    private TrackingEventRepository trackingEventRepository;

    @Autowired
    private LocationHistoryRepository locationHistoryRepository;

    @Autowired
    private DriverSessionRepository driverSessionRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    // ========================================
    // 1. Record tracking update
    // ========================================
    @Transactional
    public TrackingEvent recordLocation(Map<String, Object> request) {
        Long shipmentId = Long.valueOf(request.get("shipmentId").toString());
        String trackingId = request.get("trackingId").toString();
        String status = request.get("status").toString();
        Double latitude = Double.valueOf(request.get("latitude").toString());
        Double longitude = Double.valueOf(request.get("longitude").toString());
        String locationName = request.get("locationName") != null 
                ? request.get("locationName").toString() : null;

        // Validate shipment
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new CustomException("Shipment not found"));

        // Create tracking event
        TrackingEvent event = new TrackingEvent();
        event.setShipmentId(shipmentId);
        event.setTrackingId(trackingId);
        event.setEventType("LOCATION_UPDATE");
        event.setStatus(status);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setLocationName(locationName);
        event.setDriverVerified(true);
        event.setEventTime(LocalDateTime.now());

        TrackingEvent savedEvent = trackingEventRepository.save(event);

        // Save location history
        LocationHistory history = new LocationHistory();
        history.setTrackingEventId(savedEvent.getId());
        history.setLatitude(latitude);
        history.setLongitude(longitude);
        history.setRecordedAt(LocalDateTime.now());
        locationHistoryRepository.save(history);

        // Update shipment status
        shipment.setStatus(status);
        shipmentRepository.save(shipment);

        return savedEvent;
    }

    // ========================================
    // 2. Get tracking info by tracking ID
    // ========================================
    public Map<String, Object> getTrackingInfo(String trackingId) {
        List<TrackingEvent> events = trackingEventRepository
                .findByTrackingIdOrderByEventTimeDesc(trackingId);

        if (events.isEmpty()) {
            throw new CustomException("No tracking info found for: " + trackingId);
        }

        TrackingEvent latest = events.get(0);

        Map<String, Object> response = new HashMap<>();
        response.put("trackingId", trackingId);
        response.put("currentStatus", latest.getStatus());
        response.put("currentLocation", latest.getLocationName());
        response.put("latitude", latest.getLatitude());
        response.put("longitude", latest.getLongitude());
        response.put("lastUpdate", latest.getEventTime());
        response.put("totalEvents", events.size());

        return response;
    }

    // ========================================
    // 3. Get all tracking events
    // ========================================
    public List<TrackingEvent> getTrackingEvents(String trackingId) {
        return trackingEventRepository.findByTrackingIdOrderByEventTimeDesc(trackingId);
    }

    // ========================================
    // 4. Start driver session
    // ========================================
    @Transactional
    public DriverSession startSession(Long partnerId, Long shipmentId) {
        DriverSession session = new DriverSession();
        session.setPartnerId(partnerId);
        session.setShipmentId(shipmentId);
        session.setSessionToken(UUID.randomUUID().toString());
        session.setActive(true);
        session.setStartedAt(LocalDateTime.now());

        return driverSessionRepository.save(session);
    }

    // ========================================
    // 5. End driver session
    // ========================================
    @Transactional
    public DriverSession endSession(String sessionToken) {
        DriverSession session = driverSessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new CustomException("Session not found"));

        session.setActive(false);
        session.setEndedAt(LocalDateTime.now());

        return driverSessionRepository.save(session);
    }

    // ========================================
    // 6. Get active sessions for partner
    // ========================================
    public List<DriverSession> getActiveSessions(Long partnerId) {
        return driverSessionRepository.findByPartnerIdAndActive(partnerId, true);
    }

    // ========================================
    // 7. Get map data (all events for tracking)
    // ========================================
    public List<TrackingEvent> getMapData(String trackingId) {
        return trackingEventRepository.findByTrackingIdOrderByEventTimeDesc(trackingId);
    }
}