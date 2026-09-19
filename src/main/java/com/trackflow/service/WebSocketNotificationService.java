package com.trackflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ========================================
    // 1. Shipment status update
    // ========================================
    public void notifyShipmentUpdate(String trackingId, String status, String location) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "SHIPMENT_UPDATE");
        message.put("trackingId", trackingId);
        message.put("status", status);
        message.put("location", location);
        message.put("timestamp", LocalDateTime.now());

        // Broadcast to shipment-specific topic
        messagingTemplate.convertAndSend("/topic/shipments/" + trackingId, message);

        // Also broadcast to global shipments topic
        messagingTemplate.convertAndSend("/topic/shipments", message);

        System.out.println("📡 WebSocket: Shipment update sent - " + trackingId + " → " + status);
    }

    // ========================================
    // 2. User notification
    // ========================================
    public void notifyUser(Long userId, String subject, String content, String type) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("userId", userId);
        message.put("subject", subject);
        message.put("content", content);
        message.put("timestamp", LocalDateTime.now());

        // Send to user-specific topic
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, message);

        System.out.println("📡 WebSocket: User notification sent - User " + userId);
    }

    // ========================================
    // 3. Delivery location update
    // ========================================
    public void notifyDriverLocation(Long shipmentId, Double latitude, Double longitude) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "LOCATION_UPDATE");
        message.put("shipmentId", shipmentId);
        message.put("latitude", latitude);
        message.put("longitude", longitude);
        message.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/tracking/" + shipmentId, message);

        System.out.println("📡 WebSocket: Location update sent - Shipment " + shipmentId);
    }

    // ========================================
    // 4. Admin dashboard updates
    // ========================================
    public void notifyAdminDashboard(String eventType, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ADMIN_UPDATE");
        payload.put("eventType", eventType);
        payload.put("message", message);
        payload.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/admin/dashboard", payload);

        System.out.println("📡 WebSocket: Admin notification - " + eventType);
    }

    // ========================================
    // 5. General broadcast
    // ========================================
    public void broadcast(String topic, Object message) {
        messagingTemplate.convertAndSend("/topic/" + topic, message);
    }
}