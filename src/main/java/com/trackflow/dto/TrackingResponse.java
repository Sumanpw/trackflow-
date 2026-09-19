package com.trackflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trackflow.entity.TrackingEvent;

import java.time.LocalDateTime;

public class TrackingResponse {

    private Long id;
    private Long shipmentId;
    private String trackingId;
    private String eventType;
    private String status;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private Boolean driverVerified;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;

    public static TrackingResponse fromEntity(TrackingEvent event) {
        TrackingResponse r = new TrackingResponse();
        r.id = event.getId();
        r.shipmentId = event.getShipmentId();
        r.trackingId = event.getTrackingId();
        r.eventType = event.getEventType();
        r.status = event.getStatus();
        r.latitude = event.getLatitude();
        r.longitude = event.getLongitude();
        r.locationName = event.getLocationName();
        r.driverVerified = event.getDriverVerified();
        r.eventTime = event.getEventTime();
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public Boolean getDriverVerified() { return driverVerified; }
    public void setDriverVerified(Boolean driverVerified) { this.driverVerified = driverVerified; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}