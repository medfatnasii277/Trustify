package com.trustify.policy_service.repository;

import com.trustify.policy_service.model.HouseInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseInsurancePolicyRepository extends JpaRepository<HouseInsurancePolicy, Long> {
    
    List<HouseInsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    List<HouseInsurancePolicy> findByUserProfileKeycloakId(String keycloakId);
    
    List<HouseInsurancePolicy> findByPropertyAddress(String propertyAddress);
    
    // Remove findByPostalCode method as postalCode field doesn't exist in the entity
}