package com.trustify.policy_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CarInsurancePolicyResponse extends InsurancePolicyResponse {
    
    private String vehicleMake;
    private String vehicleModel;
    private Integer vehicleYear;
    private String vehicleVIN;
    private String licensePlate;
    private String coverageType;
    private Boolean includesRoadSideAssistance;
    private Boolean includesRentalCarCoverage;
}