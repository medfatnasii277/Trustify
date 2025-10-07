package com.trustify.policy_service.controller;

import com.trustify.policy_service.dto.request.HouseInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.HouseInsurancePolicyResponse;
import com.trustify.policy_service.service.HouseInsurancePolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing house insurance policies
 */
@RestController
@RequestMapping("/api/policies/house")
public class HouseInsurancePolicyController {

    private final HouseInsurancePolicyService houseInsurancePolicyService;

    public HouseInsurancePolicyController(HouseInsurancePolicyService houseInsurancePolicyService) {
        this.houseInsurancePolicyService = houseInsurancePolicyService;
    }

    /**
     * Create a new house insurance policy
     *
     * @param request the house insurance policy request
     * @return the created house insurance policy
     */
    @PostMapping
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<HouseInsurancePolicyResponse> createHouseInsurancePolicy(
            @Valid @RequestBody HouseInsurancePolicyRequest request) {
        HouseInsurancePolicyResponse response = houseInsurancePolicyService.createHouseInsurancePolicy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing house insurance policy
     *
     * @param id the house insurance policy ID
     * @param request the house insurance policy request
     * @return the updated house insurance policy
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<HouseInsurancePolicyResponse> updateHouseInsurancePolicy(
            @PathVariable Long id,
            @Valid @RequestBody HouseInsurancePolicyRequest request) {
        HouseInsurancePolicyResponse response = houseInsurancePolicyService.updateHouseInsurancePolicy(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a house insurance policy by ID
     *
     * @param id the house insurance policy ID
     * @return the house insurance policy
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<HouseInsurancePolicyResponse> getHouseInsurancePolicy(@PathVariable Long id) {
        HouseInsurancePolicyResponse response = houseInsurancePolicyService.getHouseInsurancePolicy(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all house insurance policies
     *
     * @return list of all house insurance policies
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<HouseInsurancePolicyResponse>> getAllHouseInsurancePolicies() {
        List<HouseInsurancePolicyResponse> responses = houseInsurancePolicyService.getAllHouseInsurancePolicies();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get house insurance policies for current user
     *
     * @return list of house insurance policies for current user
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<List<HouseInsurancePolicyResponse>> getCurrentUserHouseInsurancePolicies() {
        List<HouseInsurancePolicyResponse> responses = houseInsurancePolicyService.getCurrentUserHouseInsurancePolicies();
        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a house insurance policy
     *
     * @param id the house insurance policy ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @houseInsurancePolicyService.isOwner(#id)")
    public ResponseEntity<Void> deleteHouseInsurancePolicy(@PathVariable Long id) {
        houseInsurancePolicyService.deleteHouseInsurancePolicy(id);
        return ResponseEntity.noContent().build();
    }
}