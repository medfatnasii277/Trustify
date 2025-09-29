package com.trustify.policy_service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("HOUSE")
@Getter
@Setter
public class HouseInsurancePolicy extends InsurancePolicy {
    
    private String propertyAddress;
    private String propertyType;
    private Integer yearBuilt;
    private BigDecimal propertyValue;
    private BigDecimal contentsValue;
    
    @Enumerated(EnumType.STRING)
    private CoverageType coverageType;
    
    private Boolean includesFloodCoverage;
    private Boolean includesEarthquakeCoverage;
    private Boolean includesLiabilityCoverage;
    
    public enum CoverageType {
        BASIC,
        STANDARD,
        PREMIUM,
        CUSTOM
    }
}