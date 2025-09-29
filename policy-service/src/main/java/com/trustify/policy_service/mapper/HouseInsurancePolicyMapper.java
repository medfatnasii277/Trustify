package com.trustify.policy_service.mapper;

import com.trustify.policy_service.dto.request.HouseInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.HouseInsurancePolicyResponse;
import com.trustify.policy_service.model.HouseInsurancePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HouseInsurancePolicyMapper {
    
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
             expression = "java(com.trustify.policy_service.model.HouseInsurancePolicy.CoverageType.valueOf(request.getCoverageType()))")
    @Mapping(target = "paymentFrequency", 
             expression = "java(com.trustify.policy_service.model.InsurancePolicy.PaymentFrequency.valueOf(request.getPaymentFrequency()))")
    HouseInsurancePolicy toEntity(HouseInsurancePolicyRequest request);
    
    @Mapping(target = "coverageType", expression = "java(housePolicy.getCoverageType().name())")
    @Mapping(target = "paymentFrequency", expression = "java(housePolicy.getPaymentFrequency().name())")
    @Mapping(target = "status", expression = "java(housePolicy.getStatus().name())")
    HouseInsurancePolicyResponse toDto(HouseInsurancePolicy housePolicy);
}