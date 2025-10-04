package com.claims.claims_service.mapper;

import com.claims.claims_service.dto.request.ClaimRequest;
import com.claims.claims_service.dto.response.ClaimResponse;
import com.claims.claims_service.model.Claim;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClaimMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimNumber", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedDate", ignore = true)
    @Mapping(target = "approvedDate", ignore = true)
    @Mapping(target = "rejectedDate", ignore = true)
    @Mapping(target = "settledDate", ignore = true)
    @Mapping(target = "approvedAmount", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Claim toEntity(ClaimRequest request);
    
    ClaimResponse toResponse(Claim claim);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimNumber", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedDate", ignore = true)
    @Mapping(target = "approvedDate", ignore = true)
    @Mapping(target = "rejectedDate", ignore = true)
    @Mapping(target = "settledDate", ignore = true)
    @Mapping(target = "approvedAmount", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "adminNotes", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ClaimRequest request, @MappingTarget Claim claim);
}
