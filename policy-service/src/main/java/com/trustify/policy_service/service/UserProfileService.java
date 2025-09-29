package com.trustify.policy_service.service;

import com.trustify.policy_service.dto.request.UserProfileRequest;
import com.trustify.policy_service.dto.response.UserProfileResponse;
import com.trustify.policy_service.model.UserProfile;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user profiles
 */
public interface UserProfileService extends BaseService<UserProfile, Long> {
    
    /**
     * Find user profile by Keycloak ID
     *
     * @param keycloakId the Keycloak ID
     * @return optional user profile
     */
    Optional<UserProfile> findByKeycloakId(String keycloakId);
    
    /**
     * Find user profile by email
     *
     * @param email the email address
     * @return optional user profile
     */
    Optional<UserProfile> findByEmail(String email);
    
    /**
     * Create a new user profile
     *
     * @param request the user profile request
     * @return the created user profile response
     */
    UserProfileResponse createUserProfile(UserProfileRequest request);
    
    /**
     * Update an existing user profile
     *
     * @param id the user profile ID
     * @param request the user profile request
     * @return the updated user profile response
     */
    UserProfileResponse updateUserProfile(Long id, UserProfileRequest request);
    
    /**
     * Get user profile by ID
     *
     * @param id the user profile ID
     * @return the user profile response
     */
    UserProfileResponse getUserProfile(Long id);
    
    /**
     * Get current user's profile (based on authentication)
     *
     * @return the user profile response
     */
    UserProfileResponse getCurrentUserProfile();
    
    /**
     * Get all user profiles
     *
     * @return list of user profile responses
     */
    List<UserProfileResponse> getAllUserProfiles();
    
    /**
     * Delete a user profile
     *
     * @param id the user profile ID
     */
    void deleteUserProfile(Long id);
}