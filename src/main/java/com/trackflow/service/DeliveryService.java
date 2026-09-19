package com.trackflow.service;

import com.trackflow.entity.Delivery;
import com.trackflow.entity.DeliveryPartner;
import com.trackflow.entity.DeliveryReceipt;
import com.trackflow.entity.Shipment;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.DeliveryPartnerRepository;
import com.trackflow.repository.DeliveryReceiptRepository;
import com.trackflow.repository.DeliveryRepository;
import com.trackflow.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryPartnerRepository partnerRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryReceiptRepository receiptRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    // ========================================
    // PARTNER MANAGEMENT
    // ========================================

    public DeliveryPartner createPartner(DeliveryPartner partner) {
        // Generate partner code
        String partnerCode;
        do {
            partnerCode = "DP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (partnerRepository.existsByPartnerCode(partnerCode));

        partner.setPartnerCode(partnerCode);
        partner.setAvailable(true);
        partner.setVerified(false);
        partner.setRating(0.0);
        partner.setTotalDeliveries(0);

        return partnerRepository.save(partner);
    }

    public DeliveryPartner getPartnerById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Delivery partner not found"));
    }

    public List<DeliveryPartner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public List<DeliveryPartner> getAvailablePartners() {
        return partnerRepository.findByAvailableAndVerified(true, true);
    }

    public DeliveryPartner updatePartner(Long id, DeliveryPartner details) {
        DeliveryPartner partner = getPartnerById(id);

        partner.setFullName(details.getFullName());
        partner.setPhone(details.getPhone());
        partner.setEmail(details.getEmail());
        partner.setVehicleType(details.getVehicleType());
        partner.setVehicleNumber(details.getVehicleNumber());
        partner.setLicenseNumber(details.getLicenseNumber());
        partner.setAvailable(details.getAvailable());

        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        if (!partnerRepository.existsById(id)) {
            throw new CustomException("Delivery partner not found");
        }
        partnerRepository.deleteById(id);
    }

    // ========================================
    // DELIVERY ASSIGNMENT
    // ========================================

    @Transactional
    public Delivery assignDelivery(Long shipmentId, Long partnerId) {
        // Validate shipment
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new CustomException("Shipment not found"));

        // Validate partner
        DeliveryPartner partner = getPartnerById(partnerId);
        if (!partner.getAvailable()) {
            throw new CustomException("Delivery partner is not available");
        }

        // Check if shipment already assigned
        if (deliveryRepository.findByShipmentId(shipmentId).isPresent()) {
            throw new CustomException("Shipment already assigned to a delivery partner");
        }

        // Create delivery record
        Delivery delivery = new Delivery();
        delivery.setShipmentId(shipmentId);
        delivery.setPartnerId(partnerId);
        delivery.setAssignmentStatus("ASSIGNED");
        delivery.setCustomerOtp(generateOtp());

        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Update shipment status
        shipment.setStatus("PAYMENT_CONFIRMED");
        shipmentRepository.save(shipment);

        // Mark partner as unavailable
        partner.setAvailable(false);
        partnerRepository.save(partner);

        return savedDelivery;
    }

    // ========================================
    // DELIVERY OPERATIONS
    // ========================================

    @Transactional
    public Delivery markPickedUp(Long deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        delivery.setAssignmentStatus("PICKED_UP");
        delivery.setPickupTime(LocalDateTime.now());

        // Update shipment status
        updateShipmentStatus(delivery.getShipmentId(), "PICKED_UP");

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery markInTransit(Long deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setAssignmentStatus("IN_TRANSIT");
        updateShipmentStatus(delivery.getShipmentId(), "IN_TRANSIT");
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery markOutForDelivery(Long deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setAssignmentStatus("OUT_FOR_DELIVERY");
        updateShipmentStatus(delivery.getShipmentId(), "OUT_FOR_DELIVERY");
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery completeDelivery(Long deliveryId, String otp, String recipientName,
                                     String photoUrl, String signatureUrl) {
        Delivery delivery = getDeliveryById(deliveryId);

        // Verify OTP
        if (otp != null && !otp.equals(delivery.getCustomerOtp())) {
            throw new CustomException("Invalid OTP");
        }

        delivery.setAssignmentStatus("DELIVERED");
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setActualDeliveryTime(LocalDateTime.now());
        delivery.setDeliveryPhotoUrl(photoUrl);
        delivery.setDeliverySignatureUrl(signatureUrl);

        Delivery saved = deliveryRepository.save(delivery);

        // Create delivery receipt
        DeliveryReceipt receipt = new DeliveryReceipt();
        receipt.setDeliveryId(deliveryId);
        receipt.setReceiptNumber("RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        receipt.setRecipientName(recipientName);
        receipt.setReceiptPhotoUrl(photoUrl);
        receipt.setRecipientSignature(signatureUrl);
        receipt.setReceivedBy(recipientName);
        receiptRepository.save(receipt);

        // Update shipment status
        updateShipmentStatus(delivery.getShipmentId(), "DELIVERED");

        // Mark partner as available again
        DeliveryPartner partner = getPartnerById(delivery.getPartnerId());
        partner.setAvailable(true);
        partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);
        partnerRepository.save(partner);

        return saved;
    }

    @Transactional
    public Delivery failDelivery(Long deliveryId, String reason) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setAssignmentStatus("FAILED");
        delivery.setFailureReason(reason);
        updateShipmentStatus(delivery.getShipmentId(), "DELIVERY_FAILED");

        // Free up partner
        DeliveryPartner partner = getPartnerById(delivery.getPartnerId());
        partner.setAvailable(true);
        partnerRepository.save(partner);

        return deliveryRepository.save(delivery);
    }

    // ========================================
    // HELPERS
    // ========================================

    public Delivery getDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new CustomException("Delivery not found"));
    }

    public Delivery getDeliveryByShipmentId(Long shipmentId) {
        return deliveryRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new CustomException("Delivery not found for shipment"));
    }

    public List<Delivery> getDeliveriesByPartner(Long partnerId) {
        return deliveryRepository.findByPartnerId(partnerId);
    }

    public List<Delivery> getDeliveriesByStatus(String status) {
        return deliveryRepository.findByAssignmentStatus(status);
    }

    public DeliveryReceipt getReceiptByDelivery(Long deliveryId) {
        return receiptRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new CustomException("Receipt not found"));
    }

    private void updateShipmentStatus(Long shipmentId, String status) {
        shipmentRepository.findById(shipmentId).ifPresent(shipment -> {
            shipment.setStatus(status);
            if ("DELIVERED".equals(status)) {
                shipment.setActualDeliveryDate(LocalDateTime.now());
            }
            shipmentRepository.save(shipment);
        });
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 999999));
    }
}