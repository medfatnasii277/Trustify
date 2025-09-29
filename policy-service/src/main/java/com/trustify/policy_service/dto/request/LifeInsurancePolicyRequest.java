package com.trustify.policy_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LifeInsurancePolicyRequest extends InsurancePolicyRequest {
    
    @NotNull(message = "Policy type is required")
    private String policyType;
    
    @NotNull(message = "Critical illness coverage option is required")
    private Boolean includesCriticalIllness;
    
    @NotNull(message = "Disability benefit option is required")
    private Boolean includesDisabilityBenefit;
    
    @NotBlank(message = "Beneficiary name is required")
    private String beneficiaryName;
    
    @NotBlank(message = "Beneficiary relationship is required")
    private String beneficiaryRelationship;
}