package com.trustify.policy_service.repository;

import com.trustify.policy_service.model.CarInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarInsurancePolicyRepository extends JpaRepository<CarInsurancePolicy, Long> {
    
    List<CarInsurancePolicy> findByUserProfile(UserProfile userProfile);
    
    List<CarInsurancePolicy> findByUserProfileKeycloakId(String keycloakId);
    
    List<CarInsurancePolicy> findByVehicleVIN(String vin);
    
    List<CarInsurancePolicy> findByLicensePlate(String licensePlate);
}