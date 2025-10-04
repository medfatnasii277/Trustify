package com.claims.claims_service.controller;

import com.claims.claims_service.dto.request.ClaimApprovalRequest;
import com.claims.claims_service.dto.request.ClaimRejectionRequest;
import com.claims.claims_service.dto.response.ClaimResponse;
import com.claims.claims_service.model.Claim;
import com.claims.claims_service.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminClaimController
 * Tests admin claim management operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminClaimController Unit Tests")
class AdminClaimControllerTest {

    @Mock
    private ClaimService claimService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AdminClaimController adminClaimController;

    private ClaimResponse claimResponse;
    private String adminUserId;
    private String claimNumber;

    @BeforeEach
    void setUp() {
        adminUserId = "admin-user-456";
        claimNumber = "CLM-12345678-ABCD1234";
        
        // Setup claim response
        claimResponse = new ClaimResponse();
        claimResponse.setId(1L);
        claimResponse.setClaimNumber(claimNumber);
        claimResponse.setPolicyNumber("CAR-2024-001");
        claimResponse.setPolicyType(Claim.PolicyType.CAR);
        claimResponse.setClaimType(Claim.ClaimType.ACCIDENT_CLAIM);
        claimResponse.setStatus(Claim.ClaimStatus.SUBMITTED);
        claimResponse.setIncidentDate(LocalDate.of(2024, 10, 1));
        claimResponse.setSubmittedDate(LocalDateTime.now());
        claimResponse.setClaimedAmount(new BigDecimal("5000.00"));
        claimResponse.setDescription("Car accident on highway");
        claimResponse.setSeverity(Claim.Severity.MEDIUM);
        claimResponse.setCreatedAt(LocalDateTime.now());
        claimResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get all claims as admin")
    void getAllClaims_Success() {
        // Given
        ClaimResponse claim2 = new ClaimResponse();
        claim2.setId(2L);
        claim2.setClaimNumber("CLM-87654321-DCBA4321");
        claim2.setStatus(Claim.ClaimStatus.APPROVED);
        
        List<ClaimResponse> claims = Arrays.asList(claimResponse, claim2);
        when(claimService.getAllClaims()).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = adminClaimController.getAllClaims();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        verify(claimService, times(1)).getAllClaims();
    }

    @Test
    @DisplayName("Should get claims by status as admin")
    void getClaimsByStatus_Success() {
        // Given
        Claim.ClaimStatus status = Claim.ClaimStatus.SUBMITTED;
        List<ClaimResponse> claims = Arrays.asList(claimResponse);
        when(claimService.getClaimsByStatus(status)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = adminClaimController.getClaimsByStatus(status);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
        
        verify(claimService, times(1)).getClaimsByStatus(status);
    }

    @Test
    @DisplayName("Should move claim to under review")
    void moveToUnderReview_Success() {
        // Given
        when(jwt.getSubject()).thenReturn(adminUserId);
        claimResponse.setStatus(Claim.ClaimStatus.UNDER_REVIEW);
        claimResponse.setReviewedBy(adminUserId);
        when(claimService.moveToUnderReview(claimNumber, adminUserId)).thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = adminClaimController.moveToUnderReview(claimNumber, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(Claim.ClaimStatus.UNDER_REVIEW);
        assertThat(response.getBody().getReviewedBy()).isEqualTo(adminUserId);
        
        verify(claimService, times(1)).moveToUnderReview(claimNumber, adminUserId);
    }

    @Test
    @DisplayName("Should approve a claim")
    void approveClaim_Success() {
        // Given
        when(jwt.getSubject()).thenReturn(adminUserId);
        ClaimApprovalRequest approvalRequest = new ClaimApprovalRequest();
        approvalRequest.setClaimNumber(claimNumber);
        approvalRequest.setApprovedAmount(new BigDecimal("4500.00"));
        approvalRequest.setAdminNotes("Claim approved after review");
        
        claimResponse.setStatus(Claim.ClaimStatus.APPROVED);
        claimResponse.setApprovedAmount(new BigDecimal("4500.00"));
        claimResponse.setApprovedDate(LocalDate.now());
        claimResponse.setAdminNotes("Claim approved after review");
        claimResponse.setReviewedBy(adminUserId);
        
        when(claimService.approveClaim(any(ClaimApprovalRequest.class), eq(adminUserId)))
                .thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = adminClaimController.approveClaim(claimNumber, approvalRequest, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(Claim.ClaimStatus.APPROVED);
        assertThat(response.getBody().getApprovedAmount()).isEqualByComparingTo(new BigDecimal("4500.00"));
        assertThat(response.getBody().getApprovedDate()).isNotNull();
        assertThat(response.getBody().getAdminNotes()).isEqualTo("Claim approved after review");
        
        verify(claimService, times(1)).approveClaim(any(ClaimApprovalRequest.class), eq(adminUserId));
    }

    @Test
    @DisplayName("Should reject a claim")
    void rejectClaim_Success() {
        // Given
        when(jwt.getSubject()).thenReturn(adminUserId);
        ClaimRejectionRequest rejectionRequest = new ClaimRejectionRequest();
        rejectionRequest.setClaimNumber(claimNumber);
        rejectionRequest.setRejectionReason("Insufficient documentation provided");
        
        claimResponse.setStatus(Claim.ClaimStatus.REJECTED);
        claimResponse.setRejectedDate(LocalDate.now());
        claimResponse.setRejectionReason("Insufficient documentation provided");
        claimResponse.setReviewedBy(adminUserId);
        
        when(claimService.rejectClaim(any(ClaimRejectionRequest.class), eq(adminUserId)))
                .thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = adminClaimController.rejectClaim(claimNumber, rejectionRequest, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(Claim.ClaimStatus.REJECTED);
        assertThat(response.getBody().getRejectedDate()).isNotNull();
        assertThat(response.getBody().getRejectionReason()).isEqualTo("Insufficient documentation provided");
        
        verify(claimService, times(1)).rejectClaim(any(ClaimRejectionRequest.class), eq(adminUserId));
    }

    @Test
    @DisplayName("Should settle a claim")
    void settleClaim_Success() {
        // Given
        when(jwt.getSubject()).thenReturn(adminUserId);
        claimResponse.setStatus(Claim.ClaimStatus.SETTLED);
        claimResponse.setSettledDate(LocalDate.now());
        claimResponse.setApprovedAmount(new BigDecimal("4500.00"));
        
        when(claimService.settleClaim(claimNumber, adminUserId)).thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = adminClaimController.settleClaim(claimNumber, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(Claim.ClaimStatus.SETTLED);
        assertThat(response.getBody().getSettledDate()).isNotNull();
        
        verify(claimService, times(1)).settleClaim(claimNumber, adminUserId);
    }

    @Test
    @DisplayName("Should get claim statistics")
    void getClaimStatistics_Success() {
        // Given
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalClaims", 100);
        statistics.put("submittedClaims", 25);
        statistics.put("underReviewClaims", 15);
        statistics.put("approvedClaims", 40);
        statistics.put("rejectedClaims", 10);
        statistics.put("settledClaims", 8);
        statistics.put("cancelledClaims", 2);
        
        when(claimService.getClaimStatistics()).thenReturn(statistics);

        // When
        ResponseEntity<Map<String, Object>> response = adminClaimController.getClaimStatistics();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalClaims")).isEqualTo(100);
        assertThat(response.getBody().get("submittedClaims")).isEqualTo(25);
        assertThat(response.getBody().get("approvedClaims")).isEqualTo(40);
        
        verify(claimService, times(1)).getClaimStatistics();
    }

    @Test
    @DisplayName("Should get empty list when no claims exist")
    void getAllClaims_EmptyList() {
        // Given
        when(claimService.getAllClaims()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<ClaimResponse>> response = adminClaimController.getAllClaims();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
        
        verify(claimService, times(1)).getAllClaims();
    }

    @Test
    @DisplayName("Should get claims filtered by APPROVED status")
    void getClaimsByStatus_Approved() {
        // Given
        ClaimResponse approvedClaim = new ClaimResponse();
        approvedClaim.setId(1L);
        approvedClaim.setClaimNumber("CLM-11111111-APPR1111");
        approvedClaim.setStatus(Claim.ClaimStatus.APPROVED);
        approvedClaim.setApprovedAmount(new BigDecimal("10000.00"));
        approvedClaim.setApprovedDate(LocalDate.now());
        
        List<ClaimResponse> claims = Arrays.asList(approvedClaim);
        when(claimService.getClaimsByStatus(Claim.ClaimStatus.APPROVED)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = adminClaimController.getClaimsByStatus(Claim.ClaimStatus.APPROVED);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getStatus()).isEqualTo(Claim.ClaimStatus.APPROVED);
        assertThat(response.getBody().get(0).getApprovedDate()).isNotNull();
        
        verify(claimService, times(1)).getClaimsByStatus(Claim.ClaimStatus.APPROVED);
    }

    @Test
    @DisplayName("Should approve claim with lower amount than claimed")
    void approveClaim_WithReducedAmount() {
        // Given
        when(jwt.getSubject()).thenReturn(adminUserId);
        ClaimApprovalRequest approvalRequest = new ClaimApprovalRequest();
        approvalRequest.setClaimNumber(claimNumber);
        approvalRequest.setApprovedAmount(new BigDecimal("3000.00")); // Less than claimed 5000
        approvalRequest.setAdminNotes("Partial approval based on assessment");
        
        claimResponse.setStatus(Claim.ClaimStatus.APPROVED);
        claimResponse.setClaimedAmount(new BigDecimal("5000.00"));
        claimResponse.setApprovedAmount(new BigDecimal("3000.00"));
        claimResponse.setAdminNotes("Partial approval based on assessment");
        
        when(claimService.approveClaim(any(ClaimApprovalRequest.class), eq(adminUserId)))
                .thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = adminClaimController.approveClaim(claimNumber, approvalRequest, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getApprovedAmount()).isLessThan(response.getBody().getClaimedAmount());
        assertThat(response.getBody().getApprovedAmount()).isEqualByComparingTo(new BigDecimal("3000.00"));
        
        verify(claimService, times(1)).approveClaim(any(ClaimApprovalRequest.class), eq(adminUserId));
    }

    @Test
    @DisplayName("Should get statistics with all zeros when no claims")
    void getClaimStatistics_AllZeros() {
        // Given
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalClaims", 0);
        statistics.put("submittedClaims", 0);
        statistics.put("underReviewClaims", 0);
        statistics.put("approvedClaims", 0);
        statistics.put("rejectedClaims", 0);
        statistics.put("settledClaims", 0);
        statistics.put("cancelledClaims", 0);
        
        when(claimService.getClaimStatistics()).thenReturn(statistics);

        // When
        ResponseEntity<Map<String, Object>> response = adminClaimController.getClaimStatistics();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalClaims")).isEqualTo(0);
        assertThat(response.getBody().get("approvedClaims")).isEqualTo(0);
        
        verify(claimService, times(1)).getClaimStatistics();
    }
}
