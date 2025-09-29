package com.trustify.policy_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HouseInsurancePolicyRequest extends InsurancePolicyRequest {
    
    @NotBlank(message = "Property address is required")
    private String propertyAddress;
    
    @NotBlank(message = "Property type is required")
    private String propertyType;
    
    @NotNull(message = "Year built is required")
    @Min(value = 1800, message = "Year built must be valid")
    private Integer yearBuilt;
    
    @NotNull(message = "Property value is required")
    @Positive(message = "Property value must be positive")
    private BigDecimal propertyValue;
    
    @NotNull(message = "Contents value is required")
    @Positive(message = "Contents value must be positive")
    private BigDecimal contentsValue;
    
    @NotNull(message = "Coverage type is required")
    private String coverageType;
    
    private Boolean includesFloodCoverage = false;
    private Boolean includesEarthquakeCoverage = false;
    private Boolean includesLiabilityCoverage = true;
}