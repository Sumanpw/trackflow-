package com.trackflow.service;

import com.trackflow.entity.Payment;
import com.trackflow.entity.Transaction;
import com.trackflow.entity.Wallet;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.PaymentRepository;
import com.trackflow.repository.TransactionRepository;
import com.trackflow.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // ========================================
    // 1. Create/Get Wallet for user
    // ========================================
    @Transactional
    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUserId(userId);
                    wallet.setBalance(1000.0);  // Demo: Start with ₹1000
                    wallet.setWalletType("CUSTOMER");
                    return walletRepository.save(wallet);
                });
    }

    // ========================================
    // 2. Get wallet balance
    // ========================================
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found for user: " + userId));
    }

    // ========================================
    // 3. Add money to wallet
    // ========================================
    @Transactional
    public Wallet addMoney(Long userId, Double amount) {
        if (amount <= 0) {
            throw new CustomException("Amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);
        Double balanceBefore = wallet.getBalance();
        wallet.setBalance(balanceBefore + amount);
        wallet.setLastTransactionAt(LocalDateTime.now());
        Wallet saved = walletRepository.save(wallet);

        // Record transaction
        Transaction txn = new Transaction();
        txn.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txn.setWalletId(saved.getId());
        txn.setTransactionType("CREDIT");
        txn.setAmount(amount);
        txn.setDescription("Wallet top-up");
        txn.setBalanceBefore(balanceBefore);
        txn.setBalanceAfter(saved.getBalance());
        txn.setStatus("COMPLETED");
        transactionRepository.save(txn);

        return saved;
    }

    // ========================================
    // 4. Make payment (main entry point)
    // ========================================
    @Transactional
    public Payment makePayment(Long shipmentId, Long customerId, Double amount, 
                                String paymentMethod, String idempotencyKey) {

        // Check idempotency — prevent duplicate payments
        if (idempotencyKey != null && paymentRepository.existsByIdempotencyKey(idempotencyKey)) {
            return paymentRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> new CustomException("Idempotent payment error"));
        }

        // Get wallet
        Wallet wallet = getOrCreateWallet(customerId);

        // Check balance
        if (wallet.getBalance() < amount) {
            throw new CustomException("Insufficient balance. Available: " + wallet.getBalance());
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setPaymentId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setShipmentId(shipmentId);
        payment.setCustomerId(customerId);
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "WALLET");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setIdempotencyKey(idempotencyKey);
        payment.setPaymentStatus("PROCESSING");

        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Deduct from wallet
            Double balanceBefore = wallet.getBalance();
            wallet.setBalance(balanceBefore - amount);
            wallet.setLastTransactionAt(LocalDateTime.now());
            walletRepository.save(wallet);

            // Record transaction (DEBIT)
            Transaction txn = new Transaction();
            txn.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            txn.setWalletId(wallet.getId());
            txn.setPaymentId(savedPayment.getId());
            txn.setTransactionType("DEBIT");
            txn.setAmount(amount);
            txn.setDescription("Payment for shipment " + shipmentId);
            txn.setReferenceId(savedPayment.getPaymentId());
            txn.setBalanceBefore(balanceBefore);
            txn.setBalanceAfter(wallet.getBalance());
            txn.setStatus("COMPLETED");
            transactionRepository.save(txn);

            // Mark payment successful
            savedPayment.setPaymentStatus("SUCCESS");
            savedPayment.setSettledAt(LocalDateTime.now());
            return paymentRepository.save(savedPayment);

        } catch (Exception e) {
            savedPayment.setPaymentStatus("FAILED");
            savedPayment.setFailureReason(e.getMessage());
            paymentRepository.save(savedPayment);
            throw new CustomException("Payment failed: " + e.getMessage());
        }
    }

    // ========================================
    // 5. Get payment by ID
    // ========================================
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException("Payment not found"));
    }

    // ========================================
    // 6. Get payment by payment ID string
    // ========================================
    public Payment getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CustomException("Payment not found: " + paymentId));
    }

    // ========================================
    // 7. Get all payments
    // ========================================
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // ========================================
    // 8. Get payments by customer
    // ========================================
    public List<Payment> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    // ========================================
    // 9. Get payments by shipment
    // ========================================
    public List<Payment> getPaymentsByShipment(Long shipmentId) {
        return paymentRepository.findByShipmentId(shipmentId);
    }

    // ========================================
    // 10. Refund payment
    // ========================================
    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (!"SUCCESS".equals(payment.getPaymentStatus())) {
            throw new CustomException("Only successful payments can be refunded");
        }

        // Refund to wallet
        Wallet wallet = getWalletByUserId(payment.getCustomerId());
        Double balanceBefore = wallet.getBalance();
        wallet.setBalance(balanceBefore + payment.getAmount());
        wallet.setLastTransactionAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record refund transaction
        Transaction txn = new Transaction();
        txn.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txn.setWalletId(wallet.getId());
        txn.setPaymentId(payment.getId());
        txn.setTransactionType("REFUND");
        txn.setAmount(payment.getAmount());
        txn.setDescription("Refund for payment " + payment.getPaymentId());
        txn.setReferenceId(payment.getPaymentId());
        txn.setBalanceBefore(balanceBefore);
        txn.setBalanceAfter(wallet.getBalance());
        txn.setStatus("COMPLETED");
        transactionRepository.save(txn);

        // Update payment status
        payment.setPaymentStatus("REFUNDED");
        return paymentRepository.save(payment);
    }

    // ========================================
    // 11. Get transaction history for user
    // ========================================
    public List<Transaction> getTransactionHistory(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return transactionRepository.findByWalletId(wallet.getId());
    }
}