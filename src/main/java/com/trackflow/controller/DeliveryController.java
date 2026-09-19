package com.trackflow.controller;

import com.trackflow.dto.DeliveryPartnerRequest;
import com.trackflow.dto.DeliveryPartnerResponse;
import com.trackflow.dto.DeliveryRequest;
import com.trackflow.dto.DeliveryResponse;
import com.trackflow.entity.Delivery;
import com.trackflow.entity.DeliveryPartner;
import com.trackflow.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // ========================================
    // PARTNER ENDPOINTS
    // ========================================

    @PostMapping("/delivery-partners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryPartnerResponse> createPartner(
            @Valid @RequestBody DeliveryPartnerRequest request) {

        DeliveryPartner partner = new DeliveryPartner();
        partner.setUserId(request.getUserId());
        partner.setFullName(request.getFullName());
        partner.setPhone(request.getPhone());
        partner.setEmail(request.getEmail());
        partner.setVehicleType(request.getVehicleType());
        partner.setVehicleNumber(request.getVehicleNumber());
        partner.setLicenseNumber(request.getLicenseNumber());

        DeliveryPartner created = deliveryService.createPartner(partner);
        return new ResponseEntity<>(DeliveryPartnerResponse.fromEntity(created), HttpStatus.CREATED);
    }

    @GetMapping("/delivery-partners")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryPartnerResponse> getAllPartners() {
        return deliveryService.getAllPartners().stream()
                .map(DeliveryPartnerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/delivery-partners/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryPartnerResponse> getPartnerById(@PathVariable Long id) {
        return ResponseEntity.ok(
                DeliveryPartnerResponse.fromEntity(deliveryService.getPartnerById(id)));
    }

    @GetMapping("/delivery-partners/available")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryPartnerResponse> getAvailablePartners() {
        return deliveryService.getAvailablePartners().stream()
                .map(DeliveryPartnerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PutMapping("/delivery-partners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryPartnerResponse> updatePartner(
            @PathVariable Long id,
            @RequestBody DeliveryPartnerRequest request) {

        DeliveryPartner details = new DeliveryPartner();
        details.setFullName(request.getFullName());
        details.setPhone(request.getPhone());
        details.setEmail(request.getEmail());
        details.setVehicleType(request.getVehicleType());
        details.setVehicleNumber(request.getVehicleNumber());
        details.setLicenseNumber(request.getLicenseNumber());

        DeliveryPartner updated = deliveryService.updatePartner(id, details);
        return ResponseEntity.ok(DeliveryPartnerResponse.fromEntity(updated));
    }

    @DeleteMapping("/delivery-partners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        deliveryService.deletePartner(id);
        return ResponseEntity.ok().build();
    }

    // ========================================
    // DELIVERY ENDPOINTS
    // ========================================

    @PostMapping("/deliveries/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> assignDelivery(
            @Valid @RequestBody DeliveryRequest request) {

        Delivery delivery = deliveryService.assignDelivery(
                request.getShipmentId(), request.getPartnerId());
        return new ResponseEntity<>(DeliveryResponse.fromEntity(delivery), HttpStatus.CREATED);
    }

    @GetMapping("/deliveries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(DeliveryResponse.fromEntity(
                deliveryService.getDeliveryById(id)));
    }

    @GetMapping("/deliveries/shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> getDeliveryByShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(DeliveryResponse.fromEntity(
                deliveryService.getDeliveryByShipmentId(shipmentId)));
    }

    @GetMapping("/deliveries/partner/{partnerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public List<DeliveryResponse> getDeliveriesByPartner(@PathVariable Long partnerId) {
        return deliveryService.getDeliveriesByPartner(partnerId).stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/deliveries/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DeliveryResponse> getDeliveriesByStatus(@PathVariable String status) {
        return deliveryService.getDeliveriesByStatus(status).stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/deliveries/{id}/pickup")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> markPickedUp(@PathVariable Long id) {
        return ResponseEntity.ok(DeliveryResponse.fromEntity(deliveryService.markPickedUp(id)));
    }

    @PostMapping("/deliveries/{id}/in-transit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> markInTransit(@PathVariable Long id) {
        return ResponseEntity.ok(DeliveryResponse.fromEntity(deliveryService.markInTransit(id)));
    }

    @PostMapping("/deliveries/{id}/out-for-delivery")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> markOutForDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(DeliveryResponse.fromEntity(
                deliveryService.markOutForDelivery(id)));
    }

    @PostMapping("/deliveries/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> completeDelivery(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Delivery delivery = deliveryService.completeDelivery(
                id,
                request.get("otp"),
                request.get("recipientName"),
                request.get("photoUrl"),
                request.get("signatureUrl"));
        return ResponseEntity.ok(DeliveryResponse.fromEntity(delivery));
    }

    @PostMapping("/deliveries/{id}/fail")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<DeliveryResponse> failDelivery(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(DeliveryResponse.fromEntity(
                deliveryService.failDelivery(id, request.get("reason"))));
    }
}