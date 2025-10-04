package com.claims.claims_service.dto.response;

import com.claims.claims_service.model.Claim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    
    private Long id;
    private String claimNumber;
    private String policyNumber;
    private Claim.PolicyType policyType;
    private Claim.ClaimType claimType;
    private Claim.ClaimStatus status;
    private LocalDate incidentDate;
    private LocalDateTime submittedDate;
    private LocalDate approvedDate;
    private LocalDate rejectedDate;
    private LocalDate settledDate;
    private BigDecimal claimedAmount;
    private BigDecimal approvedAmount;
    private String description;
    private String incidentLocation;
    private String rejectionReason;
    private String adminNotes;
    private String documentsPath;
    private String reviewedBy;
    private Claim.Severity severity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
