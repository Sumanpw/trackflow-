package com.trackflow.controller;

import com.trackflow.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {

    @Autowired
    private WebSocketNotificationService wsService;

    // ========================================
    // REST endpoints to trigger WebSocket messages
    // ========================================

    // 1. Send shipment update (ADMIN or DELIVERY_PARTNER)
    @PostMapping("/notify/shipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<Map<String, String>> notifyShipment(@RequestBody Map<String, String> request) {
        String trackingId = request.get("trackingId");
        String status = request.get("status");
        String location = request.get("location");

        wsService.notifyShipmentUpdate(trackingId, status, location);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Shipment update sent");
        response.put("trackingId", trackingId);
        return ResponseEntity.ok(response);
    }

    // 2. Send user notification (ADMIN)
    @PostMapping("/notify/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> notifyUser(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String subject = request.get("subject").toString();
        String content = request.get("content").toString();
        String type = request.getOrDefault("type", "INFO").toString();

        wsService.notifyUser(userId, subject, content, type);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User notification sent");
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    // 3. Send driver location (DELIVERY_PARTNER)
    @PostMapping("/notify/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<Map<String, Object>> notifyLocation(@RequestBody Map<String, Object> request) {
        Long shipmentId = Long.valueOf(request.get("shipmentId").toString());
        Double latitude = Double.valueOf(request.get("latitude").toString());
        Double longitude = Double.valueOf(request.get("longitude").toString());

        wsService.notifyDriverLocation(shipmentId, latitude, longitude);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Location update sent");
        response.put("shipmentId", shipmentId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // STOMP endpoint for client messages
    // ========================================
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing(String message) {
        return "Pong: " + message;
    }
}