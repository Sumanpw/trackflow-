package com.trackflow.controller;

import com.trackflow.dto.TrackingRequest;
import com.trackflow.dto.TrackingResponse;
import com.trackflow.entity.TrackingEvent;
import com.trackflow.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @PostMapping("/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<TrackingResponse> updateLocation(@Valid @RequestBody TrackingRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("shipmentId", request.getShipmentId());
        map.put("trackingId", request.getTrackingId());
        map.put("status", request.getStatus());
        map.put("latitude", request.getLatitude());
        map.put("longitude", request.getLongitude());
        map.put("locationName", request.getLocationName());

        TrackingEvent event = trackingService.recordLocation(map);
        return new ResponseEntity<>(TrackingResponse.fromEntity(event), HttpStatus.CREATED);
    }

    @GetMapping("/{trackingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Map<String, Object>> getTrackingInfo(@PathVariable String trackingId) {
        return ResponseEntity.ok(trackingService.getTrackingInfo(trackingId));
    }

    @GetMapping("/{trackingId}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<TrackingResponse> getTrackingEvents(@PathVariable String trackingId) {
        return trackingService.getTrackingEvents(trackingId).stream()
                .map(TrackingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{trackingId}/map")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<TrackingResponse> getMapData(@PathVariable String trackingId) {
        return trackingService.getMapData(trackingId).stream()
                .map(TrackingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/session/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<Object> startSession(@RequestBody Map<String, Long> request) {
        return new ResponseEntity<>(
                trackingService.startSession(request.get("partnerId"), request.get("shipmentId")),
                HttpStatus.CREATED);
    }

    @PostMapping("/session/end")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<Object> endSession(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(trackingService.endSession(request.get("sessionToken")));
    }

    @GetMapping("/session/active/{partnerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<Object> getActiveSessions(@PathVariable Long partnerId) {
        return ResponseEntity.ok(trackingService.getActiveSessions(partnerId));
    }
}