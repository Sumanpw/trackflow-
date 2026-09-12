package com.trackflow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_id", unique = true, nullable = false, length = 50)
    private String trackingId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "pickup_address_id", nullable = false)
    private Long pickupAddressId;

    @Column(name = "delivery_address_id", nullable = false)
    private Long deliveryAddressId;

    @Column(name = "shipment_status", nullable = false, length = 30)
    private String status = "CREATED";

    @Column(name = "package_type", length = 30)
    private String packageType;

    @Column(name = "total_weight")
    private Double totalWeight;

    @Column(name = "delivery_charge")
    private Double deliveryCharge;

    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @Column(name = "is_fragile")
    private Boolean fragile = false;

    @Column(name = "is_priority")
    private Boolean priority = false;

    @Column(name = "qr_code_path", length = 255)
    private String qrCodePath;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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