package com.trustify.policy_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LifeInsurancePolicyResponse extends InsurancePolicyResponse {
    
    private String policyType;
    private Boolean includesCriticalIllness;
    private Boolean includesDisabilityBenefit;
    private String beneficiaryName;
    private String beneficiaryRelationship;
}