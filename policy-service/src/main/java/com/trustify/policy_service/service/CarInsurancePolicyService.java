package com.trustify.policy_service.service;

import com.trustify.policy_service.dto.request.CarInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.CarInsurancePolicyResponse;
import com.trustify.policy_service.model.CarInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;

import java.util.List;

/**
 * Service for managing car insurance policies
 */
public interface CarInsurancePolicyService extends BaseService<CarInsurancePolicy, Long> {
    
    /**
     * Find car insurance policies by user profile
     *
     * @param userProfile the user profile
     * @return list of car insurance policies
     */
    List<CarInsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    /**
     * Find car insurance policies by user's Keycloak ID
     *
     * @param keycloakId the Keycloak ID
     * @return list of car insurance policies
     */
    List<CarInsurancePolicy> findByKeycloakId(String keycloakId);
    
    /**
     * Find car insurance policies by vehicle VIN
     *
     * @param vin the vehicle VIN
     * @return list of car insurance policies
     */
    List<CarInsurancePolicy> findByVehicleVIN(String vin);
    
    /**
     * Find car insurance policies by license plate
     *
     * @param licensePlate the license plate
     * @return list of car insurance policies
     */
    List<CarInsurancePolicy> findByLicensePlate(String licensePlate);
    
    /**
     * Create a new car insurance policy
     *
     * @param request the car insurance policy request
     * @return the created car insurance policy response
     */
    CarInsurancePolicyResponse createCarInsurancePolicy(CarInsurancePolicyRequest request);
    
    /**
     * Update an existing car insurance policy
     *
     * @param id the car insurance policy ID
     * @param request the car insurance policy request
     * @return the updated car insurance policy response
     */
    CarInsurancePolicyResponse updateCarInsurancePolicy(Long id, CarInsurancePolicyRequest request);
    
    /**
     * Get car insurance policy by ID
     *
     * @param id the car insurance policy ID
     * @return the car insurance policy response
     */
    CarInsurancePolicyResponse getCarInsurancePolicy(Long id);
    
    /**
     * Get all car insurance policies
     *
     * @return list of car insurance policy responses
     */
    List<CarInsurancePolicyResponse> getAllCarInsurancePolicies();
    
    /**
     * Get car insurance policies for current user
     *
     * @return list of car insurance policy responses
     */
    List<CarInsurancePolicyResponse> getCurrentUserCarInsurancePolicies();
    
    /**
     * Delete a car insurance policy
     *
     * @param id the car insurance policy ID
     */
    void deleteCarInsurancePolicy(Long id);
}