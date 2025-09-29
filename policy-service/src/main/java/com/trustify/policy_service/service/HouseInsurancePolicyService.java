package com.trustify.policy_service.service;

import com.trustify.policy_service.dto.request.HouseInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.HouseInsurancePolicyResponse;
import com.trustify.policy_service.model.HouseInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;

import java.util.List;

/**
 * Service for managing house insurance policies
 */
public interface HouseInsurancePolicyService extends BaseService<HouseInsurancePolicy, Long> {
    
    /**
     * Find house insurance policies by user profile
     *
     * @param userProfile the user profile
     * @return list of house insurance policies
     */
    List<HouseInsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    /**
     * Find house insurance policies by user's Keycloak ID
     *
     * @param keycloakId the Keycloak ID
     * @return list of house insurance policies
     */
    List<HouseInsurancePolicy> findByKeycloakId(String keycloakId);
    
    /**
     * Find house insurance policies by property address
     *
     * @param propertyAddress the property address
     * @return list of house insurance policies
     */
    List<HouseInsurancePolicy> findByPropertyAddress(String propertyAddress);
    
    /**
     * Find house insurance policies by postal code
     *
     * @param postalCode the postal code
     * @return list of house insurance policies
     */
    List<HouseInsurancePolicy> findByPostalCode(String postalCode);
    
    /**
     * Create a new house insurance policy
     *
     * @param request the house insurance policy request
     * @return the created house insurance policy response
     */
    HouseInsurancePolicyResponse createHouseInsurancePolicy(HouseInsurancePolicyRequest request);
    
    /**
     * Update an existing house insurance policy
     *
     * @param id the house insurance policy ID
     * @param request the house insurance policy request
     * @return the updated house insurance policy response
     */
    HouseInsurancePolicyResponse updateHouseInsurancePolicy(Long id, HouseInsurancePolicyRequest request);
    
    /**
     * Get house insurance policy by ID
     *
     * @param id the house insurance policy ID
     * @return the house insurance policy response
     */
    HouseInsurancePolicyResponse getHouseInsurancePolicy(Long id);
    
    /**
     * Get all house insurance policies
     *
     * @return list of house insurance policy responses
     */
    List<HouseInsurancePolicyResponse> getAllHouseInsurancePolicies();
    
    /**
     * Get house insurance policies for current user
     *
     * @return list of house insurance policy responses
     */
    List<HouseInsurancePolicyResponse> getCurrentUserHouseInsurancePolicies();
    
    /**
     * Delete a house insurance policy
     *
     * @param id the house insurance policy ID
     */
    void deleteHouseInsurancePolicy(Long id);
}