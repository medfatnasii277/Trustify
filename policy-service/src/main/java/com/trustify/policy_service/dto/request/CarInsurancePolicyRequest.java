package com.trustify.policy_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CarInsurancePolicyRequest extends InsurancePolicyRequest {
    
    @NotBlank(message = "Vehicle make is required")
    private String vehicleMake;
    
    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;
    
    @NotNull(message = "Vehicle year is required")
    @Min(value = 1900, message = "Vehicle year must be valid")
    private Integer vehicleYear;
    
    @NotBlank(message = "Vehicle VIN is required")
    @Pattern(regexp = "^[A-HJ-NPR-Za-hj-npr-z\\d]{17}$", message = "Invalid VIN format")
    private String vehicleVIN;
    
    @NotBlank(message = "License plate is required")
    private String licensePlate;
    
    @NotNull(message = "Coverage type is required")
    private String coverageType;
    
    private Boolean includesRoadSideAssistance = false;
    private Boolean includesRentalCarCoverage = false;
}