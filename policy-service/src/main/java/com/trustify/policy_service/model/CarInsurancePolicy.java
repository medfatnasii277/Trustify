package com.trustify.policy_service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("CAR")
@Getter
@Setter
public class CarInsurancePolicy extends InsurancePolicy {
    
    private String vehicleMake;
    private String vehicleModel;
    private Integer vehicleYear;
    private String vehicleVIN;
    private String licensePlate;
    
    @Enumerated(EnumType.STRING)
    private CoverageType coverageType;
    
    private Boolean includesRoadSideAssistance;
    private Boolean includesRentalCarCoverage;
    
    public enum CoverageType {
        LIABILITY_ONLY,
        COLLISION,
        COMPREHENSIVE,
        FULL_COVERAGE
    }
}