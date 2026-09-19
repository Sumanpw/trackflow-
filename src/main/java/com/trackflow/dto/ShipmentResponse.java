package com.trackflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trackflow.entity.Shipment;

import java.time.LocalDateTime;

public class ShipmentResponse {

    private Long id;
    private String trackingId;
    private Long customerId;
    private Long pickupAddressId;
    private Long deliveryAddressId;
    private String status;
    private String packageType;
    private Double totalWeight;
    private Double deliveryCharge;
    private String specialInstructions;
    private Boolean fragile;
    private Boolean priority;
    private String qrCodePath;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedDeliveryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualDeliveryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static ShipmentResponse fromEntity(Shipment shipment) {
        ShipmentResponse response = new ShipmentResponse();
        response.id = shipment.getId();
        response.trackingId = shipment.getTrackingId();
        response.customerId = shipment.getCustomerId();
        response.pickupAddressId = shipment.getPickupAddressId();
        response.deliveryAddressId = shipment.getDeliveryAddressId();
        response.status = shipment.getStatus();
        response.packageType = shipment.getPackageType();
        response.totalWeight = shipment.getTotalWeight();
        response.deliveryCharge = shipment.getDeliveryCharge();
        response.specialInstructions = shipment.getSpecialInstructions();
        response.fragile = shipment.getFragile();
        response.priority = shipment.getPriority();
        response.qrCodePath = shipment.getQrCodePath();
        response.estimatedDeliveryDate = shipment.getEstimatedDeliveryDate();
        response.actualDeliveryDate = shipment.getActualDeliveryDate();
        response.createdAt = shipment.getCreatedAt();
        response.updatedAt = shipment.getUpdatedAt();
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPickupAddressId() { return pickupAddressId; }
    public void setPickupAddressId(Long pickupAddressId) { this.pickupAddressId = pickupAddressId; }

    public Long getDeliveryAddressId() { return deliveryAddressId; }
    public void setDeliveryAddressId(Long deliveryAddressId) { this.deliveryAddressId = deliveryAddressId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public Double getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(Double deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public Boolean getFragile() { return fragile; }
    public void setFragile(Boolean fragile) { this.fragile = fragile; }

    public Boolean getPriority() { return priority; }
    public void setPriority(Boolean priority) { this.priority = priority; }

    public String getQrCodePath() { return qrCodePath; }
    public void setQrCodePath(String qrCodePath) { this.qrCodePath = qrCodePath; }

    public LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }

    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}