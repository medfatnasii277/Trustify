package com.trustify.policy_service.mapper;

import com.trustify.policy_service.dto.request.UserProfileRequest;
import com.trustify.policy_service.dto.response.UserProfileResponse;
import com.trustify.policy_service.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", expression = "java(calculateAge(request.getDateOfBirth()))")
    @Mapping(target = "profileCompleted", constant = "true")
    UserProfile toEntity(UserProfileRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "age", expression = "java(calculateAge(request.getDateOfBirth()))")
    void updateEntityFromRequest(UserProfileRequest request, @MappingTarget UserProfile userProfile);

    UserProfileResponse toResponse(UserProfile userProfile);

    default Integer calculateAge(java.time.LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
    }
}