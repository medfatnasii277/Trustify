package com.trustify.policy_service.controller;

import com.trustify.policy_service.dto.request.UserProfileRequest;
import com.trustify.policy_service.dto.response.UserProfileResponse;
import com.trustify.policy_service.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user profiles
 */
@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Create a new user profile
     *
     * @param request the user profile request
     * @return the created user profile
     */
    @PostMapping
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<UserProfileResponse> createUserProfile(@Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.createUserProfile(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing user profile
     *
     * @param id the user profile ID
     * @param request the user profile request
     * @return the updated user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.updateUserProfile(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a user profile by ID
     *
     * @param id the user profile ID
     * @return the user profile
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        UserProfileResponse response = userProfileService.getUserProfile(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current user's profile
     *
     * @return the current user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        UserProfileResponse response = userProfileService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all user profiles
     *
     * @return list of all user profiles
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles() {
        List<UserProfileResponse> responses = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a user profile
     *
     * @param id the user profile ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @userProfileService.isOwner(#id)")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }
}