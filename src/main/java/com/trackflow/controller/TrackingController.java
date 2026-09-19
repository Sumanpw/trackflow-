package com.trackflow.controller;

import com.trackflow.entity.DriverSession;
import com.trackflow.entity.TrackingEvent;
import com.trackflow.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    // ========================================
    // 1. Update location (DELIVERY_PARTNER)
    // ========================================
    @PostMapping("/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<TrackingEvent> updateLocation(@RequestBody Map<String, Object> request) {
        return new ResponseEntity<>(
                trackingService.recordLocation(request),
                HttpStatus.CREATED);
    }

    // ========================================
    // 2. Get tracking info (any auth)
    // ========================================
    @GetMapping("/{trackingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Map<String, Object>> getTrackingInfo(@PathVariable String trackingId) {
        return ResponseEntity.ok(trackingService.getTrackingInfo(trackingId));
    }

    // ========================================
    // 3. Get all events for shipment
    // ========================================
    @GetMapping("/{trackingId}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<TrackingEvent> getTrackingEvents(@PathVariable String trackingId) {
        return trackingService.getTrackingEvents(trackingId);
    }

    // ========================================
    // 4. Start driver session
    // ========================================
    @PostMapping("/session/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DriverSession> startSession(@RequestBody Map<String, Long> request) {
        Long partnerId = request.get("partnerId");
        Long shipmentId = request.get("shipmentId");
        return new ResponseEntity<>(
                trackingService.startSession(partnerId, shipmentId),
                HttpStatus.CREATED);
    }

    // ========================================
    // 5. End driver session
    // ========================================
    @PostMapping("/session/end")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DriverSession> endSession(@RequestBody Map<String, String> request) {
        String sessionToken = request.get("sessionToken");
        return ResponseEntity.ok(trackingService.endSession(sessionToken));
    }

    // ========================================
    // 6. Get active sessions
    // ========================================
    @GetMapping("/session/active/{partnerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public List<DriverSession> getActiveSessions(@PathVariable Long partnerId) {
        return trackingService.getActiveSessions(partnerId);
    }

    // ========================================
    // 7. Get map data
    // ========================================
    @GetMapping("/{trackingId}/map")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<TrackingEvent> getMapData(@PathVariable String trackingId) {
        return trackingService.getMapData(trackingId);
    }
}