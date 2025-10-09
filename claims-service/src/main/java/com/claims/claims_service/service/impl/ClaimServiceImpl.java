package com.claims.claims_service.service.impl;

import com.claims.claims_service.dto.request.ClaimApprovalRequest;
import com.claims.claims_service.dto.request.ClaimRejectionRequest;
import com.claims.claims_service.dto.request.ClaimRequest;
import com.claims.claims_service.dto.response.ClaimResponse;
import com.claims.claims_service.event.ClaimStatusChangedEvent;
import com.claims.claims_service.exception.InvalidClaimOperationException;
import com.claims.claims_service.exception.ResourceNotFoundException;
import com.claims.claims_service.kafka.ClaimEventPublisher;
import com.claims.claims_service.mapper.ClaimMapper;
import com.claims.claims_service.model.Claim;
import com.claims.claims_service.repository.ClaimRepository;
import com.claims.claims_service.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaimServiceImpl implements ClaimService {
    
    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final ClaimEventPublisher claimEventPublisher;
    
    @Override
    public ClaimResponse submitClaim(ClaimRequest request, String keycloakUserId) {
        log.info("Submitting new claim for user: {}", keycloakUserId);
        
        Claim claim = claimMapper.toEntity(request);
        claim.setKeycloakUserId(keycloakUserId);
        claim.setClaimNumber(generateClaimNumber());
        claim.setStatus(Claim.ClaimStatus.SUBMITTED);
        claim.setSubmittedDate(LocalDateTime.now());
        
        if (claim.getSeverity() == null) {
            claim.setSeverity(Claim.Severity.MEDIUM);
        }
        
        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim created successfully with number: {}", savedClaim.getClaimNumber());
        
        return claimMapper.toResponse(savedClaim);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimByNumber(String claimNumber, String keycloakUserId) {
        log.info("Fetching claim by number: {} for user: {}", claimNumber, keycloakUserId);
        
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with number: " + claimNumber));
        
        // Verify the claim belongs to the user
        if (!claim.getKeycloakUserId().equals(keycloakUserId)) {
            throw new InvalidClaimOperationException("You don't have permission to view this claim");
        }
        
        return claimMapper.toResponse(claim);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyMyClaims(String keycloakUserId) {
        log.info("Fetching all claims for user: {}", keycloakUserId);
        
        List<Claim> claims = claimRepository.findByKeycloakUserId(keycloakUserId);
        return claims.stream()
                .map(claimMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaimsByStatus(String keycloakUserId, Claim.ClaimStatus status) {
        log.info("Fetching claims for user: {} with status: {}", keycloakUserId, status);
        
        List<Claim> claims = claimRepository.findByKeycloakUserIdAndStatus(keycloakUserId, status);
        return claims.stream()
                .map(claimMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaimsByPolicyType(String keycloakUserId, Claim.PolicyType policyType) {
        log.info("Fetching claims for user: {} with policy type: {}", keycloakUserId, policyType);
        
        List<Claim> claims = claimRepository.findByKeycloakUserIdAndPolicyType(keycloakUserId, policyType);
        return claims.stream()
                .map(claimMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByPolicyNumber(String policyNumber, String keycloakUserId) {
        log.info("Fetching claims for policy: {} and user: {}", policyNumber, keycloakUserId);
        
        List<Claim> claims = claimRepository.findByPolicyNumber(policyNumber);
        
        // Filter to only show user's own claims
        List<Claim> userClaims = claims.stream()
                .filter(claim -> claim.getKeycloakUserId().equals(keycloakUserId))
                .collect(Collectors.toList());
        
        return userClaims.stream()
                .map(claimMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClaimResponse cancelClaim(String claimNumber, String keycloakUserId) {
        log.info("Cancelling claim: {} for user: {}", claimNumber, keycloakUserId);
        
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with number: " + claimNumber));
        
        // Verify ownership
        if (!claim.getKeycloakUserId().equals(keycloakUserId)) {
            throw new InvalidClaimOperationException("You don't have permission to cancel this claim");
        }
        
        // Can only cancel if in SUBMITTED or UNDER_REVIEW status
        if (claim.getStatus() != Claim.ClaimStatus.SUBMITTED && 
            claim.getStatus() != Claim.ClaimStatus.UNDER_REVIEW) {
            throw new InvalidClaimOperationException(
                "Claim can only be cancelled if in SUBMITTED or UNDER_REVIEW status. Current status: " + claim.getStatus()
            );
        }
        
        claim.setStatus(Claim.ClaimStatus.CANCELLED);
        Claim savedClaim = claimRepository.save(claim);
        
        log.info("Claim cancelled successfully: {}", claimNumber);
        return claimMapper.toResponse(savedClaim);
    }
    
    // Admin Methods
    
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {
        log.info("Admin: Fetching all claims");
        
        List<Claim> claims = claimRepository.findAll();
        return claims.stream()
                .map(claimMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByStatus(Claim.ClaimStatus status) {
        log.info("Admin: Fetching claims with status: {}", status);
        
        List<Claim> claims = claimRepository.findByStatus(status);
        return claims.stream()
                .map(claimMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClaimResponse moveToUnderReview(String claimNumber, String adminUserId) {
        log.info("Admin: Moving claim to under review: {}", claimNumber);
        
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with number: " + claimNumber));
        
        if (claim.getStatus() != Claim.ClaimStatus.SUBMITTED) {
            throw new InvalidClaimOperationException(
                "Claim can only be moved to UNDER_REVIEW from SUBMITTED status. Current status: " + claim.getStatus()
            );
        }
        
        claim.setStatus(Claim.ClaimStatus.UNDER_REVIEW);
        claim.setReviewedBy(adminUserId);
        
        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim moved to under review: {}", claimNumber);
        
        return claimMapper.toResponse(savedClaim);
    }
    
    @Override
    public ClaimResponse approveClaim(ClaimApprovalRequest request, String adminUserId) {
        log.info("Admin: Approving claim: {}", request.getClaimNumber());
        
        Claim claim = claimRepository.findByClaimNumber(request.getClaimNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with number: " + request.getClaimNumber()));
        
        if (claim.getStatus() != Claim.ClaimStatus.UNDER_REVIEW) {
            throw new InvalidClaimOperationException(
                "Claim can only be approved from UNDER_REVIEW status. Current status: " + claim.getStatus()
            );
        }
        
        claim.setStatus(Claim.ClaimStatus.APPROVED);
        claim.setApprovedAmount(request.getApprovedAmount());
        claim.setApprovedDate(LocalDate.now());
        claim.setReviewedBy(adminUserId);
        
        if (request.getAdminNotes() != null) {
            claim.setAdminNotes(request.getAdminNotes());
        }
        
        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim approved successfully: {}", request.getClaimNumber());
        
        // Publish Kafka event
        publishClaimStatusChangeEvent(savedClaim, "UNDER_REVIEW", "APPROVED", adminUserId, null);
        
        return claimMapper.toResponse(savedClaim);
    }
    
    @Override
    public ClaimResponse rejectClaim(ClaimRejectionRequest request, String adminUserId) {
        log.info("Admin: Rejecting claim: {}", request.getClaimNumber());
        
        Claim claim = claimRepository.findByClaimNumber(request.getClaimNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with number: " + request.getClaimNumber()));
        
        if (claim.getStatus() != Claim.ClaimStatus.UNDER_REVIEW) {
            throw new InvalidClaimOperationException(
                "Claim can only be rejected from UNDER_REVIEW status. Current status: " + claim.getStatus()
            );
        }
        
        claim.setStatus(Claim.ClaimStatus.REJECTED);
        claim.setRejectionReason(request.getRejectionReason());
        claim.setRejectedDate(LocalDate.now());
        claim.setReviewedBy(adminUserId);
        
        if (request.getAdminNotes() != null) {
            claim.setAdminNotes(request.getAdminNotes());
        }
        
        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim rejected successfully: {}", request.getClaimNumber());
        
        // Publish Kafka event
        publishClaimStatusChangeEvent(savedClaim, "UNDER_REVIEW", "REJECTED", adminUserId, request.getRejectionReason());
        
        return claimMapper.toResponse(savedClaim);
    }
    
    @Override
    public ClaimResponse settleClaim(String claimNumber, String adminUserId) {
        log.info("Admin: Settling claim: {}", claimNumber);
        
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with number: " + claimNumber));
        
        if (claim.getStatus() != Claim.ClaimStatus.APPROVED) {
            throw new InvalidClaimOperationException(
                "Claim can only be settled from APPROVED status. Current status: " + claim.getStatus()
            );
        }
        
        claim.setStatus(Claim.ClaimStatus.SETTLED);
        claim.setSettledDate(LocalDate.now());
        claim.setReviewedBy(adminUserId);
        
        Claim savedClaim = claimRepository.save(claim);
        log.info("Claim settled successfully: {}", claimNumber);
        
        return claimMapper.toResponse(savedClaim);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getClaimStatistics() {
        log.info("Admin: Fetching claim statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalClaims", claimRepository.count());
        stats.put("submittedClaims", claimRepository.countByStatus(Claim.ClaimStatus.SUBMITTED));
        stats.put("underReviewClaims", claimRepository.countByStatus(Claim.ClaimStatus.UNDER_REVIEW));
        stats.put("approvedClaims", claimRepository.countByStatus(Claim.ClaimStatus.APPROVED));
        stats.put("rejectedClaims", claimRepository.countByStatus(Claim.ClaimStatus.REJECTED));
        stats.put("settledClaims", claimRepository.countByStatus(Claim.ClaimStatus.SETTLED));
        stats.put("cancelledClaims", claimRepository.countByStatus(Claim.ClaimStatus.CANCELLED));
        
        return stats;
    }
    
    // Helper method to generate unique claim number
    private String generateClaimNumber() {
        String prefix = "CLM";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + timestamp.substring(timestamp.length() - 8) + "-" + randomPart;
    }
    
    /**
     * Helper method to publish claim status change event to Kafka
     */
    private void publishClaimStatusChangeEvent(Claim claim, String oldStatus, String newStatus, 
                                                 String adminUserId, String reason) {
        ClaimStatusChangedEvent event = new ClaimStatusChangedEvent();
        event.setClaimNumber(claim.getClaimNumber());
        event.setOldStatus(oldStatus);
        event.setNewStatus(newStatus);
        event.setUserId(claim.getKeycloakUserId());
        event.setUserEmail(claim.getEmail());  // Assuming email field exists
        event.setTimestamp(LocalDateTime.now());
        event.setChangedBy(adminUserId);
        event.setReason(reason);
        
        claimEventPublisher.publishClaimStatusChanged(event);
    }
}
