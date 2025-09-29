package com.trustify.policy_service.service;

import com.trustify.policy_service.model.InsurancePolicy;
import com.trustify.policy_service.model.UserProfile;

import java.util.List;

/**
 * Base service interface for all insurance policy types
 */
public interface InsurancePolicyService extends BaseService<InsurancePolicy, Long> {
    
    /**
     * Find policies by user profile
     *
     * @param userProfile the user profile
     * @return list of insurance policies
     */
    List<InsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    /**
     * Find policies by user's Keycloak ID
     *
     * @param keycloakId the Keycloak ID
     * @return list of insurance policies
     */
    List<InsurancePolicy> findByKeycloakId(String keycloakId);
    
    /**
     * Find active policies
     *
     * @return list of active insurance policies
     */
    List<InsurancePolicy> findActivePolicies();
    
    /**
     * Find policies by policy number
     *
     * @param policyNumber the policy number
     * @return list of insurance policies
     */
    List<InsurancePolicy> findByPolicyNumber(String policyNumber);
}