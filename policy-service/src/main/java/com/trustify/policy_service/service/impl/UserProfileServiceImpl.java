package com.trustify.policy_service.service.impl;

import com.trustify.policy_service.dto.request.UserProfileRequest;
import com.trustify.policy_service.dto.response.UserProfileResponse;
import com.trustify.policy_service.exception.ResourceNotFoundException;
import com.trustify.policy_service.mapper.UserProfileMapper;
import com.trustify.policy_service.model.UserProfile;
import com.trustify.policy_service.repository.UserProfileRepository;
import com.trustify.policy_service.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserProfileService
 */
@Service
@Slf4j
@Transactional
public class UserProfileServiceImpl extends BaseServiceImpl<UserProfile, Long, UserProfileRepository> implements UserProfileService {

    private final UserProfileMapper userProfileMapper;

    public UserProfileServiceImpl(UserProfileRepository repository, UserProfileMapper userProfileMapper) {
        super(repository);
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public Optional<UserProfile> findByKeycloakId(String keycloakId) {
        return repository.findByKeycloakId(keycloakId);
    }

    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public UserProfileResponse createUserProfile(UserProfileRequest request) {
        log.info("Creating user profile: {}", request);
        UserProfile userProfile = userProfileMapper.toEntity(request);
        
        // Set Keycloak ID from authenticated user if not provided
        if (userProfile.getKeycloakId() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                userProfile.setKeycloakId(jwt.getSubject());
            }
        }
        
        UserProfile savedProfile = repository.save(userProfile);
        log.info("User profile created with ID: {}", savedProfile.getId());
        return userProfileMapper.toResponse(savedProfile);
    }

    @Override
    public UserProfileResponse updateUserProfile(Long id, UserProfileRequest request) {
        log.info("Updating user profile ID: {}", id);
        UserProfile existingProfile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + id));
        
        // Update profile fields
        userProfileMapper.updateEntityFromRequest(request, existingProfile);
        
        UserProfile updatedProfile = repository.save(existingProfile);
        log.info("User profile updated: {}", updatedProfile.getId());
        return userProfileMapper.toResponse(updatedProfile);
    }

    @Override
    public UserProfileResponse getUserProfile(Long id) {
        log.info("Fetching user profile ID: {}", id);
        UserProfile userProfile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + id));
        return userProfileMapper.toResponse(userProfile);
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        String keycloakId = getCurrentUserKeycloakId();
        log.info("Fetching current user profile with Keycloak ID: {}", keycloakId);
        
        UserProfile userProfile = repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for current user"));
        
        return userProfileMapper.toResponse(userProfile);
    }

    @Override
    public List<UserProfileResponse> getAllUserProfiles() {
        log.info("Fetching all user profiles");
        return repository.findAll().stream()
                .map(userProfileMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserProfile(Long id) {
        log.info("Deleting user profile ID: {}", id);
        repository.deleteById(id);
    }
    
    /**
     * Get Keycloak ID of current authenticated user
     *
     * @return the Keycloak ID
     */
    private String getCurrentUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getSubject();
        }
        throw new ResourceNotFoundException("User not authenticated or not using JWT authentication");
    }
}