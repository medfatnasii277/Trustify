package com.trustify.policy_service.mapper;

import com.trustify.policy_service.dto.request.CarInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.CarInsurancePolicyResponse;
import com.trustify.policy_service.model.CarInsurancePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CarInsurancePolicyMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "policyNumber", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "premiumAmount", ignore = true)
    @Mapping(target = "coverageType", 
             expression = "java(com.trustify.policy_service.model.CarInsurancePolicy.CoverageType.valueOf(request.getCoverageType()))")
    @Mapping(target = "paymentFrequency", 
             expression = "java(com.trustify.policy_service.model.InsurancePolicy.PaymentFrequency.valueOf(request.getPaymentFrequency()))")
    CarInsurancePolicy toEntity(CarInsurancePolicyRequest request);
    
    @Mapping(target = "coverageType", expression = "java(carPolicy.getCoverageType().name())")
    @Mapping(target = "paymentFrequency", expression = "java(carPolicy.getPaymentFrequency().name())")
    @Mapping(target = "status", expression = "java(carPolicy.getStatus().name())")
    CarInsurancePolicyResponse toDto(CarInsurancePolicy carPolicy);
}