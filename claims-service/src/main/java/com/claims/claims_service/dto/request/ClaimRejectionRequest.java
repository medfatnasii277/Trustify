package com.claims.claims_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRejectionRequest {
    
    @NotBlank(message = "Claim number is required")
    private String claimNumber;
    
    @NotBlank(message = "Rejection reason is required")
    @Size(min = 10, max = 1000, message = "Rejection reason must be between 10 and 1000 characters")
    private String rejectionReason;
    
    @Size(max = 1000, message = "Admin notes cannot exceed 1000 characters")
    private String adminNotes;
}
