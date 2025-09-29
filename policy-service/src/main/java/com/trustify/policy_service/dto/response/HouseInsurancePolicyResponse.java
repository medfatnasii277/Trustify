package com.trustify.policy_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HouseInsurancePolicyResponse extends InsurancePolicyResponse {
    
    private String propertyAddress;
    private String propertyType;
    private Integer yearBuilt;
    private BigDecimal propertyValue;
    private BigDecimal contentsValue;
    private String coverageType;
    private Boolean includesFloodCoverage;
    private Boolean includesEarthquakeCoverage;
    private Boolean includesLiabilityCoverage;
}