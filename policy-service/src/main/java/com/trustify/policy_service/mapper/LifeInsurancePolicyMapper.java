package com.trustify.policy_service.mapper;

import com.trustify.policy_service.dto.request.LifeInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.LifeInsurancePolicyResponse;
import com.trustify.policy_service.model.LifeInsurancePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LifeInsurancePolicyMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "policyNumber", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "premiumAmount", ignore = true)
    @Mapping(target = "policyType", 
             expression = "java(com.trustify.policy_service.model.LifeInsurancePolicy.LifePolicyType.valueOf(request.getPolicyType()))")
    @Mapping(target = "paymentFrequency", 
             expression = "java(com.trustify.policy_service.model.InsurancePolicy.PaymentFrequency.valueOf(request.getPaymentFrequency()))")
    LifeInsurancePolicy toEntity(LifeInsurancePolicyRequest request);
    
    @Mapping(target = "policyType", expression = "java(lifePolicy.getPolicyType().name())")
    @Mapping(target = "paymentFrequency", expression = "java(lifePolicy.getPaymentFrequency().name())")
    @Mapping(target = "status", expression = "java(lifePolicy.getStatus().name())")
    LifeInsurancePolicyResponse toResponse(LifeInsurancePolicy lifePolicy);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "policyNumber", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "policyType", 
             expression = "java(com.trustify.policy_service.model.LifeInsurancePolicy.LifePolicyType.valueOf(request.getPolicyType()))")
    @Mapping(target = "paymentFrequency", 
             expression = "java(com.trustify.policy_service.model.InsurancePolicy.PaymentFrequency.valueOf(request.getPaymentFrequency()))")
    void updateEntityFromRequest(LifeInsurancePolicyRequest request, @org.mapstruct.MappingTarget LifeInsurancePolicy entity);
}