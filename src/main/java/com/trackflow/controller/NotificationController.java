package com.trackflow.controller;

import com.trackflow.entity.Notification;
import com.trackflow.entity.NotificationTemplate;
import com.trackflow.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ========================================
    // 1. Send notification (ADMIN or system)
    // ========================================
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> sendNotification(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long shipmentId = request.get("shipmentId") != null
                ? Long.valueOf(request.get("shipmentId").toString()) : null;
        String notificationType = request.get("notificationType").toString();
        String channel = request.get("channel").toString();
        String subject = request.get("subject") != null
                ? request.get("subject").toString() : null;
        String content = request.get("content").toString();
        String priority = request.get("priority") != null
                ? request.get("priority").toString() : "MEDIUM";

        Notification notification = notificationService.sendNotification(
                userId, shipmentId, notificationType, channel, subject, content, priority);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    // ========================================
    // 2. Send using template
    // ========================================
    @PostMapping("/send-template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> sendFromTemplate(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long shipmentId = request.get("shipmentId") != null
                ? Long.valueOf(request.get("shipmentId").toString()) : null;
        String templateCode = request.get("templateCode").toString();

        @SuppressWarnings("unchecked")
        Map<String, String> variables = (Map<String, String>) request.get("variables");

        Notification notification = notificationService.sendFromTemplate(
                userId, shipmentId, templateCode, variables);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    // ========================================
    // 3. Get user notifications
    // ========================================
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    // ========================================
    // 4. Get unread notifications
    // ========================================
    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    // ========================================
    // 5. Get notification by ID
    // ========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    // ========================================
    // 6. Mark as read
    // ========================================
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // ========================================
    // 7. Mark all as read for user
    // ========================================
    @PutMapping("/read-all/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Long userId) {
        int count = notificationService.markAllAsRead(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notifications marked as read");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 8. Create template (ADMIN)
    // ========================================
    @PostMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplate> createTemplate(
            @RequestBody NotificationTemplate template) {
        return new ResponseEntity<>(
                notificationService.createTemplate(template),
                HttpStatus.CREATED);
    }

    // ========================================
    // 9. Get all templates (ADMIN)
    // ========================================
    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificationTemplate> getAllTemplates() {
        return notificationService.getAllTemplates();
    }

    // ========================================
    // 10. Get active templates
    // ========================================
    @GetMapping("/templates/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<NotificationTemplate> getActiveTemplates() {
        return notificationService.getActiveTemplates();
    }
}