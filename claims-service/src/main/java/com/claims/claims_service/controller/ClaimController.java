package com.claims.claims_service.controller;

import com.claims.claims_service.dto.request.ClaimRequest;
import com.claims.claims_service.dto.response.ClaimResponse;
import com.claims.claims_service.model.Claim;
import com.claims.claims_service.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for claim operations (user-facing endpoints)
 * Users can submit claims, view their claims, and cancel pending claims
 */
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Claims", description = "User claim management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class ClaimController {
    
    private final ClaimService claimService;
    
    /**
     * Submit a new insurance claim
     *
     * @param request the claim request containing claim details
     * @param jwt the authenticated user's JWT token
     * @return the created claim response
     */
    @PostMapping
    @Operation(summary = "Submit a new claim", description = "Submit a new insurance claim for life, car, or house insurance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Claim submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid claim data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    public ResponseEntity<ClaimResponse> submitClaim(
            @Valid @RequestBody ClaimRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        log.info("User {} is submitting a new claim for policy {}", userId, request.getPolicyNumber());
        
        ClaimResponse response = claimService.submitClaim(request, userId);
        log.info("Claim {} submitted successfully by user {}", response.getClaimNumber(), userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all claims for the authenticated user
     *
     * @param jwt the authenticated user's JWT token
     * @return list of user's claims
     */
    @GetMapping("/my-claims")
    @Operation(summary = "Get my claims", description = "Retrieve all claims submitted by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    public ResponseEntity<List<ClaimResponse>> getMyClaims(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.info("User {} is retrieving their claims", userId);
        
        List<ClaimResponse> claims = claimService.getMyMyClaims(userId);
        log.info("Retrieved {} claims for user {}", claims.size(), userId);
        
        return ResponseEntity.ok(claims);
    }
    
    /**
     * Get a specific claim by claim number
     *
     * @param claimNumber the unique claim number
     * @param jwt the authenticated user's JWT token
     * @return the claim details
     */
    @GetMapping("/{claimNumber}")
    @Operation(summary = "Get claim by number", description = "Retrieve a specific claim by its claim number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Claim belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponse> getClaimByNumber(
            @Parameter(description = "Claim number (e.g., CLM-1728037200000-abc123)")
            @PathVariable String claimNumber,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        log.info("User {} is retrieving claim {}", userId, claimNumber);
        
        ClaimResponse response = claimService.getClaimByNumber(claimNumber, userId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get claims by status for the authenticated user
     *
     * @param status the claim status to filter by
     * @param jwt the authenticated user's JWT token
     * @return list of claims with the specified status
     */
    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get my claims by status", description = "Retrieve user's claims filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    public ResponseEntity<List<ClaimResponse>> getMyClaimsByStatus(
            @Parameter(description = "Claim status (SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, SETTLED, CANCELLED)")
            @PathVariable Claim.ClaimStatus status,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        log.info("User {} is retrieving claims with status {}", userId, status);
        
        List<ClaimResponse> claims = claimService.getMyClaimsByStatus(userId, status);
        log.info("Retrieved {} claims with status {} for user {}", claims.size(), status, userId);
        
        return ResponseEntity.ok(claims);
    }
    
    /**
     * Get claims by policy type for the authenticated user
     *
     * @param policyType the policy type to filter by
     * @param jwt the authenticated user's JWT token
     * @return list of claims for the specified policy type
     */
    @GetMapping("/by-policy-type/{policyType}")
    @Operation(summary = "Get my claims by policy type", description = "Retrieve user's claims filtered by policy type (LIFE, CAR, HOUSE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid policy type"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    public ResponseEntity<List<ClaimResponse>> getMyClaimsByPolicyType(
            @Parameter(description = "Policy type (LIFE, CAR, HOUSE)")
            @PathVariable Claim.PolicyType policyType,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        log.info("User {} is retrieving claims for policy type {}", userId, policyType);
        
        List<ClaimResponse> claims = claimService.getMyClaimsByPolicyType(userId, policyType);
        log.info("Retrieved {} claims for policy type {} for user {}", claims.size(), policyType, userId);
        
        return ResponseEntity.ok(claims);
    }
    
    /**
     * Get all claims for a specific policy number
     *
     * @param policyNumber the policy number
     * @param jwt the authenticated user's JWT token
     * @return list of claims for the policy
     */
    @GetMapping("/by-policy/{policyNumber}")
    @Operation(summary = "Get claims by policy number", description = "Retrieve all claims for a specific policy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Policy belongs to another user")
    })
    public ResponseEntity<List<ClaimResponse>> getClaimsByPolicyNumber(
            @Parameter(description = "Policy number")
            @PathVariable String policyNumber,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        log.info("User {} is retrieving claims for policy {}", userId, policyNumber);
        
        List<ClaimResponse> claims = claimService.getClaimsByPolicyNumber(policyNumber, userId);
        log.info("Retrieved {} claims for policy {} for user {}", claims.size(), policyNumber, userId);
        
        return ResponseEntity.ok(claims);
    }
    
    /**
     * Cancel a pending claim
     * Only claims in SUBMITTED or UNDER_REVIEW status can be cancelled
     *
     * @param claimNumber the claim number to cancel
     * @param jwt the authenticated user's JWT token
     * @return the updated claim response
     */
    @PatchMapping("/{claimNumber}/cancel")
    @Operation(summary = "Cancel a claim", description = "Cancel a pending claim (only SUBMITTED or UNDER_REVIEW status)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Claim cannot be cancelled (invalid status)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Claim belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponse> cancelClaim(
            @Parameter(description = "Claim number to cancel")
            @PathVariable String claimNumber,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        log.info("User {} is cancelling claim {}", userId, claimNumber);
        
        ClaimResponse response = claimService.cancelClaim(claimNumber, userId);
        log.info("Claim {} cancelled successfully by user {}", claimNumber, userId);
        
        return ResponseEntity.ok(response);
    }
}
