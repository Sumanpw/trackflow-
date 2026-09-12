package com.trackflow.controller;

import com.trackflow.entity.Address;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.AddressRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    // Get all addresses
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<Address> getUserAddresses(@PathVariable Long userId) {
        return addressRepository.findByUserId(userId);
    }

    // Get single address
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Address> getAddress(@PathVariable Long userId, @PathVariable Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        return ResponseEntity.ok(address);
    }

    // Create address
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Address> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody Address address) {

        address.setUserId(userId);

        List<Address> existing = addressRepository.findByUserId(userId);
        if (existing.isEmpty()) {
            address.setDefault(true);
        }

        if (address.isDefault()) {
            existing.forEach(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
        }

        Address saved = addressRepository.save(address);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Update address
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Address> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long id,
            @Valid @RequestBody Address addressDetails) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        address.setAddressLine1(addressDetails.getAddressLine1());
        address.setAddressLine2(addressDetails.getAddressLine2());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCountry(addressDetails.getCountry());
        address.setAddressType(addressDetails.getAddressType());

        Address updated = addressRepository.save(address);
        return ResponseEntity.ok(updated);
    }

    // Set default
    @PutMapping("/{id}/set-default")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Address> setDefault(
            @PathVariable Long userId,
            @PathVariable Long id) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(a -> {
            a.setDefault(false);
            addressRepository.save(a);
        });

        address.setDefault(true);
        Address updated = addressRepository.save(address);
        return ResponseEntity.ok(updated);
    }

    // Delete address
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        addressRepository.delete(address);
        return ResponseEntity.ok().build();
    }
}