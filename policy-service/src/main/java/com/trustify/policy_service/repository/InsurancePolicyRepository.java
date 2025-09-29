package com.trustify.policy_service.repository;

import com.trustify.policy_service.model.InsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    
    List<InsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    List<InsurancePolicy> findByUserProfileKeycloakId(String keycloakId);
    
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    
    List<InsurancePolicy> findByStartDateBeforeAndEndDateAfter(LocalDate startDate, LocalDate endDate);
}