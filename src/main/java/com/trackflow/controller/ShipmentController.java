package com.trackflow.controller;

import com.trackflow.dto.ShipmentRequest;
import com.trackflow.dto.ShipmentResponse;
import com.trackflow.entity.Shipment;
import com.trackflow.exception.CustomException;
import com.trackflow.service.QRCodeService;
import com.trackflow.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private QRCodeService qrCodeService;

    // 1. Create Shipment
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @CacheEvict(value = {"shipments", "shipmentById", "shipmentByTracking"}, allEntries = true)
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        Shipment shipment = new Shipment();
        shipment.setCustomerId(request.getCustomerId());
        shipment.setPickupAddressId(request.getPickupAddressId());
        shipment.setDeliveryAddressId(request.getDeliveryAddressId());
        shipment.setPackageType(request.getPackageType());
        shipment.setTotalWeight(request.getTotalWeight());
        shipment.setSpecialInstructions(request.getSpecialInstructions());
        shipment.setFragile(request.getFragile() != null ? request.getFragile() : false);
        shipment.setPriority(request.getPriority() != null ? request.getPriority() : false);

        Shipment created = shipmentService.createShipment(shipment);
        return new ResponseEntity<>(ShipmentResponse.fromEntity(created), HttpStatus.CREATED);
    }

    // 2. Get Shipment by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    @Cacheable(value = "shipmentById", key = "#id")
    public ResponseEntity<ShipmentResponse> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ShipmentResponse.fromEntity(shipment));
    }

    // 3. Get by Tracking ID
    @GetMapping("/tracking/{trackingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    @Cacheable(value = "shipmentByTracking", key = "#trackingId")
    public ResponseEntity<ShipmentResponse> getShipmentByTrackingId(@PathVariable String trackingId) {
        Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
        return ResponseEntity.ok(ShipmentResponse.fromEntity(shipment));
    }

    // 4. Get All (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShipmentResponse> getAllShipments() {
        return shipmentService.getAllShipments().stream()
                .map(ShipmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 5. Get by Customer
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<ShipmentResponse> getShipmentsByCustomer(@PathVariable Long customerId) {
        return shipmentService.getShipmentsByCustomer(customerId).stream()
                .map(ShipmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 6. Get by Status (ADMIN)
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShipmentResponse> getShipmentsByStatus(@PathVariable String status) {
        return shipmentService.getShipmentsByStatus(status).stream()
                .map(ShipmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 7. Update Status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    @CacheEvict(value = {"shipments", "shipmentById", "shipmentByTracking"}, allEntries = true)
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Shipment updated = shipmentService.updateStatus(id, status);
        return ResponseEntity.ok(ShipmentResponse.fromEntity(updated));
    }

    // 8. Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"shipments", "shipmentById", "shipmentByTracking"}, allEntries = true)
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok().build();
    }

    // QR endpoints (keep as-is, they return Resource or Shipment)
    @PostMapping("/{id}/qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @CacheEvict(value = {"shipments", "shipmentById", "shipmentByTracking"}, allEntries = true)
    public ResponseEntity<ShipmentResponse> generateQRCode(@PathVariable Long id) {
        Shipment updated = shipmentService.generateQRCode(id);
        return ResponseEntity.ok(ShipmentResponse.fromEntity(updated));
    }

    @GetMapping("/{id}/qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Resource> downloadQRCode(@PathVariable Long id) {
        try {
            Shipment shipment = shipmentService.getShipmentById(id);

            if (shipment.getQrCodePath() == null) {
                shipment = shipmentService.generateQRCode(id);
            }

            String fileName = "shipment-" + id;
            Path qrPath = qrCodeService.getQRCodePath(fileName);
            Resource resource = new UrlResource(qrPath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + ".png\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}