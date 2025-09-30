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
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdBy", expression = "java(getCurrentUsername())")
    @Mapping(target = "updatedBy", expression = "java(getCurrentUsername())")
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "email", expression = "java(getCurrentUserEmail())")
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
    
    default String getCurrentUsername() {
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
                org.springframework.security.oauth2.jwt.Jwt jwt = 
                    (org.springframework.security.oauth2.jwt.Jwt) auth.getPrincipal();
                return jwt.getClaimAsString("preferred_username");
            }
        } catch (Exception e) {
            System.err.println("Error getting username: " + e.getMessage());
        }
        return "system";
    }
    
    default String getCurrentUserEmail() {
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
                org.springframework.security.oauth2.jwt.Jwt jwt = 
                    (org.springframework.security.oauth2.jwt.Jwt) auth.getPrincipal();
                return jwt.getClaimAsString("email");
            }
        } catch (Exception e) {
            System.err.println("Error getting email: " + e.getMessage());
        }
        return null;
    }
}