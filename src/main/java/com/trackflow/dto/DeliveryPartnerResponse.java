package com.trackflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trackflow.entity.DeliveryPartner;

import java.time.LocalDateTime;

public class DeliveryPartnerResponse {

    private Long id;
    private Long userId;
    private String partnerCode;
    private String fullName;
    private String phone;
    private String email;
    private String vehicleType;
    private String vehicleNumber;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean available;
    private Boolean verified;
    private Double rating;
    private Integer totalDeliveries;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static DeliveryPartnerResponse fromEntity(DeliveryPartner partner) {
        DeliveryPartnerResponse r = new DeliveryPartnerResponse();
        r.id = partner.getId();
        r.userId = partner.getUserId();
        r.partnerCode = partner.getPartnerCode();
        r.fullName = partner.getFullName();
        r.phone = partner.getPhone();
        r.email = partner.getEmail();
        r.vehicleType = partner.getVehicleType();
        r.vehicleNumber = partner.getVehicleNumber();
        r.currentLatitude = partner.getCurrentLatitude();
        r.currentLongitude = partner.getCurrentLongitude();
        r.available = partner.getAvailable();
        r.verified = partner.getVerified();
        r.rating = partner.getRating();
        r.totalDeliveries = partner.getTotalDeliveries();
        r.createdAt = partner.getCreatedAt();
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPartnerCode() { return partnerCode; }
    public void setPartnerCode(String partnerCode) { this.partnerCode = partnerCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getTotalDeliveries() { return totalDeliveries; }
    public void setTotalDeliveries(Integer totalDeliveries) { this.totalDeliveries = totalDeliveries; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}