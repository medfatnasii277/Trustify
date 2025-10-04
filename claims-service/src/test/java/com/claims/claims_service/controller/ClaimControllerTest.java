package com.claims.claims_service.controller;

import com.claims.claims_service.dto.request.ClaimRequest;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClaimController
 * Tests user-facing claim operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimController Unit Tests")
class ClaimControllerTest {

    @Mock
    private ClaimService claimService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private ClaimController claimController;

    private ClaimRequest claimRequest;
    private ClaimResponse claimResponse;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "test-user-123";
        
        // Setup mock JWT
        when(jwt.getSubject()).thenReturn(userId);
        
        // Setup claim request
        claimRequest = new ClaimRequest();
        claimRequest.setPolicyNumber("CAR-2024-001");
        claimRequest.setPolicyType(Claim.PolicyType.CAR);
        claimRequest.setClaimType(Claim.ClaimType.ACCIDENT_CLAIM);
        claimRequest.setIncidentDate(LocalDate.of(2024, 10, 1));
        claimRequest.setClaimedAmount(new BigDecimal("5000.00"));
        claimRequest.setDescription("Car accident on highway. Front bumper damaged.");
        claimRequest.setIncidentLocation("Interstate 95");
        claimRequest.setSeverity(Claim.Severity.MEDIUM);
        
        // Setup claim response
        claimResponse = new ClaimResponse();
        claimResponse.setId(1L);
        claimResponse.setClaimNumber("CLM-12345678-ABCD1234");
        claimResponse.setPolicyNumber("CAR-2024-001");
        claimResponse.setPolicyType(Claim.PolicyType.CAR);
        claimResponse.setClaimType(Claim.ClaimType.ACCIDENT_CLAIM);
        claimResponse.setStatus(Claim.ClaimStatus.SUBMITTED);
        claimResponse.setIncidentDate(LocalDate.of(2024, 10, 1));
        claimResponse.setSubmittedDate(LocalDateTime.now());
        claimResponse.setClaimedAmount(new BigDecimal("5000.00"));
        claimResponse.setDescription("Car accident on highway. Front bumper damaged.");
        claimResponse.setIncidentLocation("Interstate 95");
        claimResponse.setSeverity(Claim.Severity.MEDIUM);
        claimResponse.setCreatedAt(LocalDateTime.now());
        claimResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should submit a new claim successfully")
    void submitClaim_Success() {
        // Given
        when(claimService.submitClaim(any(ClaimRequest.class), eq(userId)))
                .thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = claimController.submitClaim(claimRequest, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getClaimNumber()).isEqualTo("CLM-12345678-ABCD1234");
        assertThat(response.getBody().getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
        
        verify(claimService, times(1)).submitClaim(any(ClaimRequest.class), eq(userId));
    }

    @Test
    @DisplayName("Should get all claims for the authenticated user")
    void getMyClaims_Success() {
        // Given
        ClaimResponse claim2 = new ClaimResponse();
        claim2.setId(2L);
        claim2.setClaimNumber("CLM-87654321-DCBA4321");
        claim2.setPolicyNumber("LIFE-2024-002");
        claim2.setPolicyType(Claim.PolicyType.LIFE);
        claim2.setStatus(Claim.ClaimStatus.APPROVED);
        
        List<ClaimResponse> claims = Arrays.asList(claimResponse, claim2);
        when(claimService.getMyMyClaims(userId)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getMyClaims(jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getClaimNumber()).isEqualTo("CLM-12345678-ABCD1234");
        assertThat(response.getBody().get(1).getClaimNumber()).isEqualTo("CLM-87654321-DCBA4321");
        
        verify(claimService, times(1)).getMyMyClaims(userId);
    }

    @Test
    @DisplayName("Should get claim by claim number")
    void getClaimByNumber_Success() {
        // Given
        String claimNumber = "CLM-12345678-ABCD1234";
        when(claimService.getClaimByNumber(claimNumber, userId)).thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = claimController.getClaimByNumber(claimNumber, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getClaimNumber()).isEqualTo(claimNumber);
        
        verify(claimService, times(1)).getClaimByNumber(claimNumber, userId);
    }

    @Test
    @DisplayName("Should get claims by status")
    void getMyClaimsByStatus_Success() {
        // Given
        Claim.ClaimStatus status = Claim.ClaimStatus.SUBMITTED;
        List<ClaimResponse> claims = Arrays.asList(claimResponse);
        when(claimService.getMyClaimsByStatus(userId, status)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getMyClaimsByStatus(status, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
        
        verify(claimService, times(1)).getMyClaimsByStatus(userId, status);
    }

    @Test
    @DisplayName("Should get claims by policy type")
    void getMyClaimsByPolicyType_Success() {
        // Given
        Claim.PolicyType policyType = Claim.PolicyType.CAR;
        List<ClaimResponse> claims = Arrays.asList(claimResponse);
        when(claimService.getMyClaimsByPolicyType(userId, policyType)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getMyClaimsByPolicyType(policyType, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getPolicyType()).isEqualTo(Claim.PolicyType.CAR);
        
        verify(claimService, times(1)).getMyClaimsByPolicyType(userId, policyType);
    }

    @Test
    @DisplayName("Should get claims by policy number")
    void getClaimsByPolicyNumber_Success() {
        // Given
        String policyNumber = "CAR-2024-001";
        List<ClaimResponse> claims = Arrays.asList(claimResponse);
        when(claimService.getClaimsByPolicyNumber(policyNumber, userId)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getClaimsByPolicyNumber(policyNumber, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getPolicyNumber()).isEqualTo(policyNumber);
        
        verify(claimService, times(1)).getClaimsByPolicyNumber(policyNumber, userId);
    }

    @Test
    @DisplayName("Should cancel a claim successfully")
    void cancelClaim_Success() {
        // Given
        String claimNumber = "CLM-12345678-ABCD1234";
        claimResponse.setStatus(Claim.ClaimStatus.CANCELLED);
        when(claimService.cancelClaim(claimNumber, userId)).thenReturn(claimResponse);

        // When
        ResponseEntity<ClaimResponse> response = claimController.cancelClaim(claimNumber, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(Claim.ClaimStatus.CANCELLED);
        
        verify(claimService, times(1)).cancelClaim(claimNumber, userId);
    }

    @Test
    @DisplayName("Should return empty list when user has no claims")
    void getMyClaims_EmptyList() {
        // Given
        when(claimService.getMyMyClaims(userId)).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getMyClaims(jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
        
        verify(claimService, times(1)).getMyMyClaims(userId);
    }

    @Test
    @DisplayName("Should get claims for LIFE insurance")
    void getMyClaimsByPolicyType_LifeInsurance() {
        // Given
        ClaimResponse lifeClaimResponse = new ClaimResponse();
        lifeClaimResponse.setId(2L);
        lifeClaimResponse.setClaimNumber("CLM-11111111-LIFE1111");
        lifeClaimResponse.setPolicyNumber("LIFE-2024-999");
        lifeClaimResponse.setPolicyType(Claim.PolicyType.LIFE);
        lifeClaimResponse.setClaimType(Claim.ClaimType.DEATH_CLAIM);
        lifeClaimResponse.setStatus(Claim.ClaimStatus.UNDER_REVIEW);
        
        List<ClaimResponse> claims = Arrays.asList(lifeClaimResponse);
        when(claimService.getMyClaimsByPolicyType(userId, Claim.PolicyType.LIFE)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getMyClaimsByPolicyType(Claim.PolicyType.LIFE, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getPolicyType()).isEqualTo(Claim.PolicyType.LIFE);
        assertThat(response.getBody().get(0).getClaimType()).isEqualTo(Claim.ClaimType.DEATH_CLAIM);
        
        verify(claimService, times(1)).getMyClaimsByPolicyType(userId, Claim.PolicyType.LIFE);
    }

    @Test
    @DisplayName("Should get claims for HOUSE insurance")
    void getMyClaimsByPolicyType_HouseInsurance() {
        // Given
        ClaimResponse houseClaimResponse = new ClaimResponse();
        houseClaimResponse.setId(3L);
        houseClaimResponse.setClaimNumber("CLM-22222222-HOUSE222");
        houseClaimResponse.setPolicyNumber("HOUSE-2024-555");
        houseClaimResponse.setPolicyType(Claim.PolicyType.HOUSE);
        houseClaimResponse.setClaimType(Claim.ClaimType.FIRE_DAMAGE_CLAIM);
        houseClaimResponse.setStatus(Claim.ClaimStatus.APPROVED);
        houseClaimResponse.setSeverity(Claim.Severity.HIGH);
        
        List<ClaimResponse> claims = Arrays.asList(houseClaimResponse);
        when(claimService.getMyClaimsByPolicyType(userId, Claim.PolicyType.HOUSE)).thenReturn(claims);

        // When
        ResponseEntity<List<ClaimResponse>> response = claimController.getMyClaimsByPolicyType(Claim.PolicyType.HOUSE, jwt);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getPolicyType()).isEqualTo(Claim.PolicyType.HOUSE);
        assertThat(response.getBody().get(0).getClaimType()).isEqualTo(Claim.ClaimType.FIRE_DAMAGE_CLAIM);
        assertThat(response.getBody().get(0).getSeverity()).isEqualTo(Claim.Severity.HIGH);
        
        verify(claimService, times(1)).getMyClaimsByPolicyType(userId, Claim.PolicyType.HOUSE);
    }
}
