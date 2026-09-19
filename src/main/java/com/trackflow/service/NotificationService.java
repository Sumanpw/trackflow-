package com.trackflow.service;

import com.trackflow.entity.Notification;
import com.trackflow.entity.NotificationTemplate;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.NotificationRepository;
import com.trackflow.repository.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    // ========================================
    // 1. Send notification
    // ========================================
    @Transactional
    public Notification sendNotification(Long userId, Long shipmentId, String notificationType,
                                          String channel, String subject, String content,
                                          String priority) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setShipmentId(shipmentId);
        notification.setNotificationType(notificationType);
        notification.setChannel(channel);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setPriority(priority != null ? priority : "MEDIUM");
        notification.setStatus("SENT");
        notification.setDeliveredAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // ========================================
    // 2. Send using template
    // ========================================
    @Transactional
    public Notification sendFromTemplate(Long userId, Long shipmentId, String templateCode,
                                          Map<String, String> variables) {
        NotificationTemplate template = templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new CustomException("Template not found: " + templateCode));

        // Replace placeholders in body
        String content = template.getBodyTemplate();
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }

        String subject = template.getSubject();
        if (subject != null && variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                subject = subject.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setShipmentId(shipmentId);
        notification.setNotificationType(template.getTemplateType());
        notification.setChannel(template.getTemplateType());
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setStatus("SENT");
        notification.setDeliveredAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // ========================================
    // 3. Get user notifications
    // ========================================
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ========================================
    // 4. Get unread notifications
    // ========================================
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndStatus(userId, "SENT");
    }

    // ========================================
    // 5. Get notification by ID
    // ========================================
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException("Notification not found"));
    }

    // ========================================
    // 6. Mark as read
    // ========================================
    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = getNotificationById(id);
        notification.setStatus("READ");
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    // ========================================
    // 7. Mark all as read for user
    // ========================================
    @Transactional
    public int markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndStatusNot(userId, "READ");

        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(n -> {
            n.setStatus("READ");
            n.setReadAt(now);
        });

        notificationRepository.saveAll(notifications);
        return notifications.size();
    }

    // ========================================
    // 8. Create template
    // ========================================
    @Transactional
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        if (templateRepository.existsByTemplateCode(template.getTemplateCode())) {
            throw new CustomException("Template already exists: " + template.getTemplateCode());
        }
        template.setActive(true);
        return templateRepository.save(template);
    }

    // ========================================
    // 9. Get all templates
    // ========================================
    public List<NotificationTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    // ========================================
    // 10. Get active templates
    // ========================================
    public List<NotificationTemplate> getActiveTemplates() {
        return templateRepository.findByActive(true);
    }

    // ========================================
    // 11. Convenience: Send shipment status notification
    // ========================================
    @Transactional
    public Notification notifyShipmentStatus(Long userId, Long shipmentId,
                                              String trackingId, String status) {
        String subject = "Shipment Update: " + trackingId;
        String content = String.format(
                "Your shipment %s status has been updated to: %s",
                trackingId, status);

        return sendNotification(userId, shipmentId, "IN_APP", "WEBSOCKET",
                subject, content, "MEDIUM");
    }
}