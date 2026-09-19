package com.trackflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trackflow.entity.Payment;

import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private String paymentId;
    private Long shipmentId;
    private Long customerId;
    private Double amount;
    private String currency;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private String failureReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime settledAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.id = payment.getId();
        response.paymentId = payment.getPaymentId();
        response.shipmentId = payment.getShipmentId();
        response.customerId = payment.getCustomerId();
        response.amount = payment.getAmount();
        response.currency = payment.getCurrency();
        response.paymentStatus = payment.getPaymentStatus();
        response.paymentMethod = payment.getPaymentMethod();
        response.transactionId = payment.getTransactionId();
        response.failureReason = payment.getFailureReason();
        response.settledAt = payment.getSettledAt();
        response.createdAt = payment.getCreatedAt();
        response.updatedAt = payment.getUpdatedAt();
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}