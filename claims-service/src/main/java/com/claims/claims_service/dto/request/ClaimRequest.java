package com.claims.claims_service.dto.request;

import com.claims.claims_service.model.Claim;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    
    @NotBlank(message = "Policy number is required")
    private String policyNumber;
    
    @NotNull(message = "Policy type is required")
    private Claim.PolicyType policyType;
    
    @NotNull(message = "Claim type is required")
    private Claim.ClaimType claimType;
    
    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    private LocalDate incidentDate;
    
    @NotNull(message = "Claimed amount is required")
    @Positive(message = "Claimed amount must be positive")
    private BigDecimal claimedAmount;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;
    
    @Size(max = 500, message = "Incident location cannot exceed 500 characters")
    private String incidentLocation;
    
    private String documentsPath;
    
    private Claim.Severity severity;
}
