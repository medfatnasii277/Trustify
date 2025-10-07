package com.trustify.policy_service.controller;

import com.trustify.policy_service.dto.request.LifeInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.LifeInsurancePolicyResponse;
import com.trustify.policy_service.service.LifeInsurancePolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing life insurance policies
 */
@RestController
@RequestMapping("/api/policies/life")
public class LifeInsurancePolicyController {

    private final LifeInsurancePolicyService lifeInsurancePolicyService;

    public LifeInsurancePolicyController(LifeInsurancePolicyService lifeInsurancePolicyService) {
        this.lifeInsurancePolicyService = lifeInsurancePolicyService;
    }

    /**
     * Create a new life insurance policy
     *
     * @param request the life insurance policy request
     * @return the created life insurance policy
     */
    @PostMapping
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<LifeInsurancePolicyResponse> createLifeInsurancePolicy(
            @Valid @RequestBody LifeInsurancePolicyRequest request) {
        LifeInsurancePolicyResponse response = lifeInsurancePolicyService.createLifeInsurancePolicy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing life insurance policy
     *
     * @param id the life insurance policy ID
     * @param request the life insurance policy request
     * @return the updated life insurance policy
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<LifeInsurancePolicyResponse> updateLifeInsurancePolicy(
            @PathVariable Long id,
            @Valid @RequestBody LifeInsurancePolicyRequest request) {
        LifeInsurancePolicyResponse response = lifeInsurancePolicyService.updateLifeInsurancePolicy(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a life insurance policy by ID
     *
     * @param id the life insurance policy ID
     * @return the life insurance policy
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<LifeInsurancePolicyResponse> getLifeInsurancePolicy(@PathVariable Long id) {
        LifeInsurancePolicyResponse response = lifeInsurancePolicyService.getLifeInsurancePolicy(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all life insurance policies
     *
     * @return list of all life insurance policies
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<LifeInsurancePolicyResponse>> getAllLifeInsurancePolicies() {
        List<LifeInsurancePolicyResponse> responses = lifeInsurancePolicyService.getAllLifeInsurancePolicies();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get life insurance policies for current user
     *
     * @return list of life insurance policies for current user
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<List<LifeInsurancePolicyResponse>> getCurrentUserLifeInsurancePolicies() {
        List<LifeInsurancePolicyResponse> responses = lifeInsurancePolicyService.getCurrentUserLifeInsurancePolicies();
        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a life insurance policy
     *
     * @param id the life insurance policy ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @lifeInsurancePolicyService.isOwner(#id)")
    public ResponseEntity<Void> deleteLifeInsurancePolicy(@PathVariable Long id) {
        lifeInsurancePolicyService.deleteLifeInsurancePolicy(id);
        return ResponseEntity.noContent().build();
    }
}