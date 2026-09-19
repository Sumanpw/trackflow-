package com.trackflow.service;

import com.trackflow.entity.Shipment;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private TrackingIdGenerator trackingIdGenerator;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private WebSocketNotificationService wsService;

    // ========================================
    // Create new shipment
    // ========================================
    public Shipment createShipment(Shipment shipment) {
        String trackingId;
        do {
            trackingId = trackingIdGenerator.generateTrackingId();
        } while (shipmentRepository.existsByTrackingId(trackingId));

        shipment.setTrackingId(trackingId);
        shipment.setStatus("CREATED");

        double charge = calculateDeliveryCharge(shipment);
        shipment.setDeliveryCharge(charge);

        shipment.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(3));

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Auto-generate QR code
        try {
            String qrPath = qrCodeService.generateQRCode(
                    savedShipment.getTrackingId(),
                    "shipment-" + savedShipment.getId()
            );
            savedShipment.setQrCodePath(qrPath);
            savedShipment = shipmentRepository.save(savedShipment);
        } catch (Exception e) {
            System.err.println("Failed to generate QR: " + e.getMessage());
        }

        // ✅ Send WebSocket notification for new shipment
        try {
            wsService.notifyShipmentUpdate(
                    savedShipment.getTrackingId(),
                    "CREATED",
                    "Shipment created"
            );
            wsService.notifyAdminDashboard(
                    "SHIPMENT_CREATED",
                    "New shipment: " + savedShipment.getTrackingId()
            );
        } catch (Exception e) {
            System.err.println("WebSocket notify failed: " + e.getMessage());
        }

        return savedShipment;
    }

    private double calculateDeliveryCharge(Shipment shipment) {
        double baseCharge = 50.0;
        double weightCharge = 0.0;
        double priorityCharge = 0.0;

        if (shipment.getTotalWeight() != null) {
            weightCharge = shipment.getTotalWeight() * 20.0;
        }

        if (Boolean.TRUE.equals(shipment.getPriority())) {
            priorityCharge = 100.0;
        }

        if (Boolean.TRUE.equals(shipment.getFragile())) {
            priorityCharge += 50.0;
        }

        return baseCharge + weightCharge + priorityCharge;
    }

    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new CustomException("Shipment not found"));
    }

    public Shipment getShipmentByTrackingId(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new CustomException("Shipment not found with tracking ID: " + trackingId));
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public List<Shipment> getShipmentsByCustomer(Long customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }

    public List<Shipment> getShipmentsByStatus(String status) {
        return shipmentRepository.findByStatus(status);
    }

    // ========================================
    // Update status — WITH WebSocket notification
    // ========================================
    public Shipment updateStatus(Long id, String newStatus) {
        Shipment shipment = getShipmentById(id);
        shipment.setStatus(newStatus);

        if ("DELIVERED".equals(newStatus)) {
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }

        Shipment updated = shipmentRepository.save(shipment);

        // ✅ Send WebSocket notification
        try {
            wsService.notifyShipmentUpdate(
                    updated.getTrackingId(),
                    newStatus,
                    "Status updated"
            );

            // Also notify the customer
            wsService.notifyUser(
                    updated.getCustomerId(),
                    "Shipment Update",
                    "Your shipment " + updated.getTrackingId() + " is now " + newStatus,
                    "SHIPMENT_STATUS"
            );

            // Notify admin dashboard
            wsService.notifyAdminDashboard(
                    "SHIPMENT_STATUS_CHANGED",
                    updated.getTrackingId() + " → " + newStatus
            );
        } catch (Exception e) {
            System.err.println("WebSocket notify failed: " + e.getMessage());
        }

        return updated;
    }

    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new CustomException("Shipment not found");
        }
        shipmentRepository.deleteById(id);

        // ✅ Notify admin
        try {
            wsService.notifyAdminDashboard("SHIPMENT_DELETED", "Shipment ID " + id + " deleted");
        } catch (Exception e) {
            System.err.println("WebSocket notify failed: " + e.getMessage());
        }
    }

    // Generate or regenerate QR code
    public Shipment generateQRCode(Long id) {
        Shipment shipment = getShipmentById(id);
        String qrPath = qrCodeService.generateQRCode(
                shipment.getTrackingId(),
                "shipment-" + shipment.getId()
        );
        shipment.setQrCodePath(qrPath);
        return shipmentRepository.save(shipment);
    }
}