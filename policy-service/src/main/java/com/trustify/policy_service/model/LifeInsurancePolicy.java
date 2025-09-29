package com.trustify.policy_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("LIFE")
@Getter
@Setter
public class LifeInsurancePolicy extends InsurancePolicy {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "life_policy_type")
    private LifePolicyType policyType;
    
    private Boolean includesCriticalIllness;
    private Boolean includesDisabilityBenefit;
    
    private String beneficiaryName;
    private String beneficiaryRelationship;
    
    public enum LifePolicyType {
        TERM,
        WHOLE_LIFE,
        UNIVERSAL_LIFE,
        VARIABLE_LIFE
    }
}