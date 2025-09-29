package com.trustify.policy_service.repository;

import com.trustify.policy_service.model.LifeInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LifeInsurancePolicyRepository extends JpaRepository<LifeInsurancePolicy, Long> {
    
    List<LifeInsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    List<LifeInsurancePolicy> findByUserProfileKeycloakId(String keycloakId);
}