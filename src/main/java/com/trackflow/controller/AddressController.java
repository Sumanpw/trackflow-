package com.trackflow.controller;

import com.trackflow.dto.AddressRequest;
import com.trackflow.dto.AddressResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    // ========================================
    // 1. Get all addresses
    // ========================================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<AddressResponse> getUserAddresses(@PathVariable Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ========================================
    // 2. Get single address
    // ========================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AddressResponse> getAddress(
            @PathVariable Long userId,
            @PathVariable Long id) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        return ResponseEntity.ok(AddressResponse.fromEntity(address));
    }

    // ========================================
    // 3. Create address
    // ========================================
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequest request) {

        Address address = new Address();
        address.setUserId(userId);
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setAddressType(request.getAddressType());
        address.setDefault(request.isDefault());

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
        return new ResponseEntity<>(AddressResponse.fromEntity(saved), HttpStatus.CREATED);
    }

    // ========================================
    // 4. Update address
    // ========================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setAddressType(request.getAddressType());

        Address updated = addressRepository.save(address);
        return ResponseEntity.ok(AddressResponse.fromEntity(updated));
    }

    // ========================================
    // 5. Set default
    // ========================================
    @PutMapping("/{id}/set-default")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AddressResponse> setDefault(
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
        return ResponseEntity.ok(AddressResponse.fromEntity(updated));
    }

    // ========================================
    // 6. Delete address
    // ========================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long id) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new CustomException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new CustomException("Address does not belong to this user");
        }

        addressRepository.delete(address);
        return ResponseEntity.ok().build();
    }
}