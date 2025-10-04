package com.claims.claims_service.service;

import com.claims.claims_service.dto.request.ClaimApprovalRequest;
import com.claims.claims_service.dto.request.ClaimRejectionRequest;
import com.claims.claims_service.dto.request.ClaimRequest;
import com.claims.claims_service.dto.response.ClaimResponse;
import com.claims.claims_service.model.Claim;

import java.util.List;
import java.util.Map;

public interface ClaimService {
    
    /**
     * Submit a new claim
     */
    ClaimResponse submitClaim(ClaimRequest request, String keycloakUserId);
    
    /**
     * Get claim by claim number
     */
    ClaimResponse getClaimByNumber(String claimNumber, String keycloakUserId);
    
    /**
     * Get all claims for the authenticated user
     */
    List<ClaimResponse> getMyMyClaims(String keycloakUserId);
    
    /**
     * Get claims by status for the authenticated user
     */
    List<ClaimResponse> getMyClaimsByStatus(String keycloakUserId, Claim.ClaimStatus status);
    
    /**
     * Get claims by policy type for the authenticated user
     */
    List<ClaimResponse> getMyClaimsByPolicyType(String keycloakUserId, Claim.PolicyType policyType);
    
    /**
     * Get claims for a specific policy number
     */
    List<ClaimResponse> getClaimsByPolicyNumber(String policyNumber, String keycloakUserId);
    
    /**
     * Cancel a claim (only if in SUBMITTED or UNDER_REVIEW status)
     */
    ClaimResponse cancelClaim(String claimNumber, String keycloakUserId);
    
    /**
     * Admin: Get all claims
     */
    List<ClaimResponse> getAllClaims();
    
    /**
     * Admin: Get claims by status
     */
    List<ClaimResponse> getClaimsByStatus(Claim.ClaimStatus status);
    
    /**
     * Admin: Move claim to under review
     */
    ClaimResponse moveToUnderReview(String claimNumber, String adminUserId);
    
    /**
     * Admin: Approve a claim
     */
    ClaimResponse approveClaim(ClaimApprovalRequest request, String adminUserId);
    
    /**
     * Admin: Reject a claim
     */
    ClaimResponse rejectClaim(ClaimRejectionRequest request, String adminUserId);
    
    /**
     * Admin: Settle a claim (mark as paid)
     */
    ClaimResponse settleClaim(String claimNumber, String adminUserId);
    
    /**
     * Get claim statistics
     */
    Map<String, Object> getClaimStatistics();
}
