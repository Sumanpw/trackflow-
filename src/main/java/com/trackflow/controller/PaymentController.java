package com.trackflow.controller;

import com.trackflow.entity.Payment;
import com.trackflow.entity.Transaction;
import com.trackflow.entity.Wallet;
import com.trackflow.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ========================================
    // WALLET ENDPOINTS
    // ========================================

    // Get wallet by user
    @GetMapping("/wallet/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getOrCreateWallet(userId));
    }

    // Add money to wallet
    @PostMapping("/wallet/{userId}/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Wallet> addMoney(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        return ResponseEntity.ok(paymentService.addMoney(userId, amount));
    }

    // ========================================
    // PAYMENT ENDPOINTS
    // ========================================

    // 1. Make payment
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Payment> makePayment(@RequestBody Map<String, Object> request) {
        Long shipmentId = Long.valueOf(request.get("shipmentId").toString());
        Long customerId = Long.valueOf(request.get("customerId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());
        String paymentMethod = request.get("paymentMethod") != null 
                ? request.get("paymentMethod").toString() : "WALLET";
        String idempotencyKey = request.get("idempotencyKey") != null 
                ? request.get("idempotencyKey").toString() : null;

        Payment payment = paymentService.makePayment(
                shipmentId, customerId, amount, paymentMethod, idempotencyKey);

        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    // 2. Get payment by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // 3. Get payment by paymentId string
    @GetMapping("/payment-id/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Payment> getPaymentByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentByPaymentId(paymentId));
    }

    // 4. Get all payments (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    // 5. Get payments by customer
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<Payment> getPaymentsByCustomer(@PathVariable Long customerId) {
        return paymentService.getPaymentsByCustomer(customerId);
    }

    // 6. Get payments by shipment
    @GetMapping("/shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<Payment> getPaymentsByShipment(@PathVariable Long shipmentId) {
        return paymentService.getPaymentsByShipment(shipmentId);
    }

    // 7. Refund payment
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    // 8. Get transaction history
    @GetMapping("/history/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<Transaction> getTransactionHistory(@PathVariable Long userId) {
        return paymentService.getTransactionHistory(userId);
    }
}