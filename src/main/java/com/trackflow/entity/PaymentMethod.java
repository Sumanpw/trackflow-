package com.trackflow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "method_type", nullable = false, length = 20)
    private String methodType;  // CARD, UPI, NETBANKING

    @Column(name = "method_token", nullable = false, length = 255)
    private String methodToken;  // Tokenized - NEVER store real card numbers!

    @Column(name = "last_four", length = 4)
    private String lastFour;  // For cards only

    @Column(name = "expiry_month", length = 2)
    private String expiryMonth;

    @Column(name = "expiry_year", length = 4)
    private String expiryYear;

    @Column(name = "is_default")
    private Boolean defaultMethod = false;

    @Column(name = "is_active")
    private Boolean active = true;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }

    public String getMethodToken() { return methodToken; }
    public void setMethodToken(String methodToken) { this.methodToken = methodToken; }

    public String getLastFour() { return lastFour; }
    public void setLastFour(String lastFour) { this.lastFour = lastFour; }

    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }

    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }

    public Boolean getDefaultMethod() { return defaultMethod; }
    public void setDefaultMethod(Boolean defaultMethod) { this.defaultMethod = defaultMethod; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}