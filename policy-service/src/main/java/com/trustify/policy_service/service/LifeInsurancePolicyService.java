package com.trustify.policy_service.service;

import com.trustify.policy_service.dto.request.LifeInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.LifeInsurancePolicyResponse;
import com.trustify.policy_service.model.LifeInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;

import java.util.List;

/**
 * Service for managing life insurance policies
 */
public interface LifeInsurancePolicyService extends BaseService<LifeInsurancePolicy, Long> {
    
    /**
     * Find life insurance policies by user profile
     *
     * @param userProfile the user profile
     * @return list of life insurance policies
     */
    List<LifeInsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    /**
     * Find life insurance policies by user's Keycloak ID
     *
     * @param keycloakId the Keycloak ID
     * @return list of life insurance policies
     */
    List<LifeInsurancePolicy> findByKeycloakId(String keycloakId);
    
    /**
     * Create a new life insurance policy
     *
     * @param request the life insurance policy request
     * @return the created life insurance policy response
     */
    LifeInsurancePolicyResponse createLifeInsurancePolicy(LifeInsurancePolicyRequest request);
    
    /**
     * Update an existing life insurance policy
     *
     * @param id the life insurance policy ID
     * @param request the life insurance policy request
     * @return the updated life insurance policy response
     */
    LifeInsurancePolicyResponse updateLifeInsurancePolicy(Long id, LifeInsurancePolicyRequest request);
    
    /**
     * Get life insurance policy by ID
     *
     * @param id the life insurance policy ID
     * @return the life insurance policy response
     */
    LifeInsurancePolicyResponse getLifeInsurancePolicy(Long id);
    
    /**
     * Get all life insurance policies
     *
     * @return list of life insurance policy responses
     */
    List<LifeInsurancePolicyResponse> getAllLifeInsurancePolicies();
    
    /**
     * Get life insurance policies for current user
     *
     * @return list of life insurance policy responses
     */
    List<LifeInsurancePolicyResponse> getCurrentUserLifeInsurancePolicies();
    
    /**
     * Delete a life insurance policy
     *
     * @param id the life insurance policy ID
     */
    void deleteLifeInsurancePolicy(Long id);
}