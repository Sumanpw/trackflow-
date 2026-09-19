package com.trackflow.controller;

import com.trackflow.dto.NotificationRequest;
import com.trackflow.dto.NotificationResponse;
import com.trackflow.entity.Notification;
import com.trackflow.entity.NotificationTemplate;
import com.trackflow.service.NotificationService;
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
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.sendNotification(
                request.getUserId(), request.getShipmentId(),
                request.getNotificationType(), request.getChannel(),
                request.getSubject(), request.getContent(), request.getPriority());
        return new ResponseEntity<>(NotificationResponse.fromEntity(notification), HttpStatus.CREATED);
    }

    @PostMapping("/send-template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> sendFromTemplate(
            @Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.sendFromTemplate(
                request.getUserId(), request.getShipmentId(),
                request.getTemplateCode(), request.getVariables());
        return new ResponseEntity<>(NotificationResponse.fromEntity(notification), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<NotificationResponse> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId).stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<NotificationResponse> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId).stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(NotificationResponse.fromEntity(
                notificationService.getNotificationById(id)));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(NotificationResponse.fromEntity(
                notificationService.markAsRead(id)));
    }

    @PutMapping("/read-all/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Long userId) {
        int count = notificationService.markAllAsRead(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notifications marked as read");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplate> createTemplate(
            @RequestBody NotificationTemplate template) {
        return new ResponseEntity<>(notificationService.createTemplate(template), HttpStatus.CREATED);
    }

    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificationTemplate> getAllTemplates() {
        return notificationService.getAllTemplates();
    }

    @GetMapping("/templates/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public List<NotificationTemplate> getActiveTemplates() {
        return notificationService.getActiveTemplates();
    }
}