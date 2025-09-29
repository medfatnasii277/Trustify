package com.trustify.policy_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class InsurancePolicyResponse {
    
    private Long id;
    private String policyNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal premiumAmount;
    private String paymentFrequency;
    private BigDecimal coverageAmount;
    private String description;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}