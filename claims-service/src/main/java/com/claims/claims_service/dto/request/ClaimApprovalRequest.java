package com.claims.claims_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApprovalRequest {
    
    @NotBlank(message = "Claim number is required")
    private String claimNumber;
    
    @Positive(message = "Approved amount must be positive")
    private BigDecimal approvedAmount;
    
    @Size(max = 1000, message = "Admin notes cannot exceed 1000 characters")
    private String adminNotes;
}
