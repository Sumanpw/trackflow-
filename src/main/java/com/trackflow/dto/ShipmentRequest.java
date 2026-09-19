package com.trackflow.dto;

import jakarta.validation.constraints.*;

public class ShipmentRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Pickup address ID is required")
    private Long pickupAddressId;

    @NotNull(message = "Delivery address ID is required")
    private Long deliveryAddressId;

    private String packageType;

    @Positive(message = "Weight must be positive")
    private Double totalWeight;

    private String specialInstructions;

    private Boolean fragile = false;

    private Boolean priority = false;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPickupAddressId() { return pickupAddressId; }
    public void setPickupAddressId(Long pickupAddressId) { this.pickupAddressId = pickupAddressId; }

    public Long getDeliveryAddressId() { return deliveryAddressId; }
    public void setDeliveryAddressId(Long deliveryAddressId) { this.deliveryAddressId = deliveryAddressId; }

    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public Boolean getFragile() { return fragile; }
    public void setFragile(Boolean fragile) { this.fragile = fragile; }

    public Boolean getPriority() { return priority; }
    public void setPriority(Boolean priority) { this.priority = priority; }
}