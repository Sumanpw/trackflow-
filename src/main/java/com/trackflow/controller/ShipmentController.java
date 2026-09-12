package com.trackflow.controller;

import com.trackflow.entity.Shipment;
import com.trackflow.service.QRCodeService;
import com.trackflow.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private QRCodeService qrCodeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Shipment> createShipment(@Valid @RequestBody Shipment shipment) {
        Shipment createdShipment = shipmentService.createShipment(shipment);
        return new ResponseEntity<>(createdShipment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    @GetMapping("/tracking/{trackingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Shipment> getShipmentByTrackingId(@PathVariable String trackingId) {
        return ResponseEntity.ok(shipmentService.getShipmentByTrackingId(trackingId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<Shipment> getShipmentsByCustomer(@PathVariable Long customerId) {
        return shipmentService.getShipmentsByCustomer(customerId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Shipment> getShipmentsByStatus(@PathVariable String status) {
        return shipmentService.getShipmentsByStatus(status);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PARTNER')")
    public ResponseEntity<Shipment> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(shipmentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // QR CODE ENDPOINTS
    // ==========================================

    // Generate/Regenerate QR code for shipment
    @PostMapping("/{id}/qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Shipment> generateQRCode(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.generateQRCode(id));
    }

    // Download QR code image
    @GetMapping("/{id}/qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER')")
    public ResponseEntity<Resource> downloadQRCode(@PathVariable Long id) {
        try {
            Shipment shipment = shipmentService.getShipmentById(id);

            if (shipment.getQrCodePath() == null) {
                // Generate if doesn't exist
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