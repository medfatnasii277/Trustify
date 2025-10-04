package com.claims.claims_service.repository;

import com.claims.claims_service.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    Optional<Claim> findByClaimNumber(String claimNumber);
    
    List<Claim> findByKeycloakUserId(String keycloakUserId);
    
    List<Claim> findByPolicyNumber(String policyNumber);
    
    List<Claim> findByStatus(Claim.ClaimStatus status);
    
    List<Claim> findByKeycloakUserIdAndStatus(String keycloakUserId, Claim.ClaimStatus status);
    
    List<Claim> findByKeycloakUserIdAndPolicyType(String keycloakUserId, Claim.PolicyType policyType);
    
    List<Claim> findByPolicyTypeAndStatus(Claim.PolicyType policyType, Claim.ClaimStatus status);
    
    long countByStatus(Claim.ClaimStatus status);
    
    long countByKeycloakUserId(String keycloakUserId);
}
