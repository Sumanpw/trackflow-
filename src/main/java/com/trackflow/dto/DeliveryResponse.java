package com.trackflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trackflow.entity.Delivery;

import java.time.LocalDateTime;

public class DeliveryResponse {

    private Long id;
    private Long shipmentId;
    private Long partnerId;
    private String assignmentStatus;
    private String deliveryPhotoUrl;
    private String deliverySignatureUrl;
    private String customerOtp;
    private String failureReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime pickupTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualDeliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static DeliveryResponse fromEntity(Delivery delivery) {
        DeliveryResponse r = new DeliveryResponse();
        r.id = delivery.getId();
        r.shipmentId = delivery.getShipmentId();
        r.partnerId = delivery.getPartnerId();
        r.assignmentStatus = delivery.getAssignmentStatus();
        r.deliveryPhotoUrl = delivery.getDeliveryPhotoUrl();
        r.deliverySignatureUrl = delivery.getDeliverySignatureUrl();
        r.customerOtp = delivery.getCustomerOtp();
        r.failureReason = delivery.getFailureReason();
        r.pickupTime = delivery.getPickupTime();
        r.deliveryTime = delivery.getDeliveryTime();
        r.actualDeliveryTime = delivery.getActualDeliveryTime();
        r.createdAt = delivery.getCreatedAt();
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }

    public String getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(String assignmentStatus) { this.assignmentStatus = assignmentStatus; }

    public String getDeliveryPhotoUrl() { return deliveryPhotoUrl; }
    public void setDeliveryPhotoUrl(String deliveryPhotoUrl) { this.deliveryPhotoUrl = deliveryPhotoUrl; }

    public String getDeliverySignatureUrl() { return deliverySignatureUrl; }
    public void setDeliverySignatureUrl(String deliverySignatureUrl) { this.deliverySignatureUrl = deliverySignatureUrl; }

    public String getCustomerOtp() { return customerOtp; }
    public void setCustomerOtp(String customerOtp) { this.customerOtp = customerOtp; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }

    public LocalDateTime getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(LocalDateTime deliveryTime) { this.deliveryTime = deliveryTime; }

    public LocalDateTime getActualDeliveryTime() { return actualDeliveryTime; }
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) { this.actualDeliveryTime = actualDeliveryTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}