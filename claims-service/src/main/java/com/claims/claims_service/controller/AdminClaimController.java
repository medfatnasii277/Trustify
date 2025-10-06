package com.claims.claims_service.controller;

import com.claims.claims_service.dto.request.ClaimApprovalRequest;
import com.claims.claims_service.dto.request.ClaimRejectionRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin claim operations
 * Admins can view all claims, approve, reject, and manage claim lifecycle
 */
@RestController
@RequestMapping("/api/admin/claims")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Claims", description = "Admin claim management endpoints")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('admin')")
public class AdminClaimController {
    
    private final ClaimService claimService;
    
    /**
     * Get all claims in the system
     *
     * @return list of all claims
     */
    @GetMapping
    @Operation(summary = "Get all claims", description = "Retrieve all claims in the system (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {
        log.info("Admin is retrieving all claims");
        
        List<ClaimResponse> claims = claimService.getAllClaims();
        log.info("Retrieved {} claims", claims.size());
        
        return ResponseEntity.ok(claims);
    }
    
    /**
     * Get all claims by status
     *
     * @param status the claim status to filter by
     * @return list of claims with the specified status
     */
    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get claims by status", description = "Retrieve all claims filtered by status (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claims retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<List<ClaimResponse>> getClaimsByStatus(
            @Parameter(description = "Claim status (SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, SETTLED, CANCELLED)")
            @PathVariable Claim.ClaimStatus status) {
        
        log.info("Admin is retrieving claims with status {}", status);
        
        List<ClaimResponse> claims = claimService.getClaimsByStatus(status);
        log.info("Retrieved {} claims with status {}", claims.size(), status);
        
        return ResponseEntity.ok(claims);
    }
    
    /**
     * Move a claim to under review status
     *
     * @param claimNumber the claim number
     * @param jwt the authenticated admin's JWT token
     * @return the updated claim response
     */
    @PatchMapping("/{claimNumber}/under-review")
    @Operation(summary = "Move claim to under review", description = "Change claim status from SUBMITTED to UNDER_REVIEW (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim moved to under review successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponse> moveToUnderReview(
            @Parameter(description = "Claim number")
            @PathVariable String claimNumber,
            @AuthenticationPrincipal Jwt jwt) {
        
        String adminUserId = jwt.getSubject();
        log.info("Admin {} is moving claim {} to under review", adminUserId, claimNumber);
        
        ClaimResponse response = claimService.moveToUnderReview(claimNumber, adminUserId);
        log.info("Claim {} moved to under review by admin {}", claimNumber, adminUserId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Approve a claim
     *
     * @param claimNumber the claim number
     * @param request the approval request with approved amount and notes
     * @param jwt the authenticated admin's JWT token
     * @return the updated claim response
     */
    @PatchMapping("/{claimNumber}/approve")
    @Operation(summary = "Approve a claim", description = "Approve a claim with approved amount and admin notes (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid approval data or status transition"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponse> approveClaim(
            @Parameter(description = "Claim number")
            @PathVariable String claimNumber,
            @Valid @RequestBody ClaimApprovalRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String adminUserId = jwt.getSubject();
        log.info("Admin {} is approving claim {}", adminUserId, claimNumber);
        
        ClaimResponse response = claimService.approveClaim(request, adminUserId);
        log.info("Claim {} approved by admin {} with amount {}", claimNumber, adminUserId, request.getApprovedAmount());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reject a claim
     *
     * @param claimNumber the claim number
     * @param request the rejection request with reason
     * @param jwt the authenticated admin's JWT token
     * @return the updated claim response
     */
    @PatchMapping("/{claimNumber}/reject")
    @Operation(summary = "Reject a claim", description = "Reject a claim with rejection reason (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid rejection data or status transition"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponse> rejectClaim(
            @Parameter(description = "Claim number")
            @PathVariable String claimNumber,
            @Valid @RequestBody ClaimRejectionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String adminUserId = jwt.getSubject();
        log.info("Admin {} is rejecting claim {}", adminUserId, claimNumber);
        
        ClaimResponse response = claimService.rejectClaim(request, adminUserId);
        log.info("Claim {} rejected by admin {}", claimNumber, adminUserId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Settle a claim (mark as paid)
     *
     * @param claimNumber the claim number
     * @param jwt the authenticated admin's JWT token
     * @return the updated claim response
     */
    @PatchMapping("/{claimNumber}/settle")
    @Operation(summary = "Settle a claim", description = "Mark an approved claim as settled/paid (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Claim settled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition - claim must be approved first"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimResponse> settleClaim(
            @Parameter(description = "Claim number")
            @PathVariable String claimNumber,
            @AuthenticationPrincipal Jwt jwt) {
        
        String adminUserId = jwt.getSubject();
        log.info("Admin {} is settling claim {}", adminUserId, claimNumber);
        
        ClaimResponse response = claimService.settleClaim(claimNumber, adminUserId);
        log.info("Claim {} settled by admin {}", claimNumber, adminUserId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get claim statistics for admin dashboard
     *
     * @return statistics map with counts by status, total amounts, etc.
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get claim statistics", description = "Retrieve claim statistics for admin dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Map<String, Object>> getClaimStatistics() {
        log.info("Admin is retrieving claim statistics");
        
        Map<String, Object> statistics = claimService.getClaimStatistics();
        log.info("Retrieved claim statistics");
        
        return ResponseEntity.ok(statistics);
    }
}
