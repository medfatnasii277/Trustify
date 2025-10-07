package com.trustify.policy_service.controller;

import com.trustify.policy_service.dto.request.CarInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.CarInsurancePolicyResponse;
import com.trustify.policy_service.service.CarInsurancePolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing car insurance policies
 */
@RestController
@RequestMapping("/api/policies/car")
public class CarInsurancePolicyController {

    private final CarInsurancePolicyService carInsurancePolicyService;

    public CarInsurancePolicyController(CarInsurancePolicyService carInsurancePolicyService) {
        this.carInsurancePolicyService = carInsurancePolicyService;
    }

    /**
     * Create a new car insurance policy
     *
     * @param request the car insurance policy request
     * @return the created car insurance policy
     */
    @PostMapping
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<CarInsurancePolicyResponse> createCarInsurancePolicy(
            @Valid @RequestBody CarInsurancePolicyRequest request) {
        CarInsurancePolicyResponse response = carInsurancePolicyService.createCarInsurancePolicy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing car insurance policy
     *
     * @param id the car insurance policy ID
     * @param request the car insurance policy request
     * @return the updated car insurance policy
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<CarInsurancePolicyResponse> updateCarInsurancePolicy(
            @PathVariable Long id,
            @Valid @RequestBody CarInsurancePolicyRequest request) {
        CarInsurancePolicyResponse response = carInsurancePolicyService.updateCarInsurancePolicy(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a car insurance policy by ID
     *
     * @param id the car insurance policy ID
     * @return the car insurance policy
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<CarInsurancePolicyResponse> getCarInsurancePolicy(@PathVariable Long id) {
        CarInsurancePolicyResponse response = carInsurancePolicyService.getCarInsurancePolicy(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all car insurance policies
     *
     * @return list of all car insurance policies
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CarInsurancePolicyResponse>> getAllCarInsurancePolicies() {
        List<CarInsurancePolicyResponse> responses = carInsurancePolicyService.getAllCarInsurancePolicies();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get car insurance policies for current user
     *
     * @return list of car insurance policies for current user
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<List<CarInsurancePolicyResponse>> getCurrentUserCarInsurancePolicies() {
        List<CarInsurancePolicyResponse> responses = carInsurancePolicyService.getCurrentUserCarInsurancePolicies();
        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a car insurance policy
     *
     * @param id the car insurance policy ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @carInsurancePolicyService.isOwner(#id)")
    public ResponseEntity<Void> deleteCarInsurancePolicy(@PathVariable Long id) {
        carInsurancePolicyService.deleteCarInsurancePolicy(id);
        return ResponseEntity.noContent().build();
    }
}