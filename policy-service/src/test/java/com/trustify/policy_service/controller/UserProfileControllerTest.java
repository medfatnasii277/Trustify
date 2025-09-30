package com.trustify.policy_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustify.policy_service.dto.request.UserProfileRequest;
import com.trustify.policy_service.dto.response.UserProfileResponse;
import com.trustify.policy_service.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the UserProfileController
 */
public class UserProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
        objectMapper.findAndRegisterModules(); // For proper handling of LocalDate
    }

    @Test
    @WithMockUser(roles = "user")
    public void testCreateUserProfile() throws Exception {
        // Prepare test data
        UserProfileRequest request = createSampleUserProfileRequest();
        UserProfileResponse response = createSampleUserProfileResponse();

        // Mock service call
        when(userProfileService.createUserProfile(any(UserProfileRequest.class))).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.email").value(response.getEmail()));
    }

    @Test
    @WithMockUser(roles = "user")
    public void testUpdateUserProfile() throws Exception {
        // Prepare test data
        Long profileId = 1L;
        UserProfileRequest request = createSampleUserProfileRequest();
        UserProfileResponse response = createSampleUserProfileResponse();

        // Mock service call
        when(userProfileService.updateUserProfile(eq(profileId), any(UserProfileRequest.class))).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(put("/api/profiles/{id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()));
    }

    @Test
    @WithMockUser(roles = "user")
    public void testGetUserProfile() throws Exception {
        // Prepare test data
        Long profileId = 1L;
        UserProfileResponse response = createSampleUserProfileResponse();

        // Mock service call
        when(userProfileService.getUserProfile(profileId)).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(get("/api/profiles/{id}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()));
    }

    @Test
    @WithMockUser(roles = "user")
    public void testGetCurrentUserProfile() throws Exception {
        // Prepare test data
        UserProfileResponse response = createSampleUserProfileResponse();

        // Mock service call
        when(userProfileService.getCurrentUserProfile()).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()));
    }

    @Test
    @WithMockUser(roles = "admin")
    public void testGetAllUserProfiles() throws Exception {
        // Prepare test data
        UserProfileResponse profile1 = createSampleUserProfileResponse();
        UserProfileResponse profile2 = createSampleUserProfileResponse();
        profile2.setId(2L);
        profile2.setFirstName("Jane");
        profile2.setLastName("Doe");
        List<UserProfileResponse> profiles = Arrays.asList(profile1, profile2);

        // Mock service call
        when(userProfileService.getAllUserProfiles()).thenReturn(profiles);

        // Perform the request and validate
        mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(profile1.getId()))
                .andExpect(jsonPath("$[0].firstName").value(profile1.getFirstName()))
                .andExpect(jsonPath("$[1].id").value(profile2.getId()))
                .andExpect(jsonPath("$[1].firstName").value(profile2.getFirstName()));
    }

    @Test
    @WithMockUser(roles = "admin")
    public void testDeleteUserProfile() throws Exception {
        // Prepare test data
        Long profileId = 1L;

        // Mock service call
        doNothing().when(userProfileService).deleteUserProfile(profileId);

        // Perform the request and validate
        mockMvc.perform(delete("/api/profiles/{id}", profileId))
                .andExpect(status().isNoContent());
    }

    /**
     * Helper method to create a sample user profile request
     */
    private UserProfileRequest createSampleUserProfileRequest() {
        UserProfileRequest request = new UserProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setPhoneNumber("+1234567890");
        request.setAddress("123 Test St");
        request.setCity("Test City");
        request.setState("Test State");
        request.setZipCode("12345");
        request.setCountry("Test Country");
        request.setEmploymentStatus("EMPLOYED");
        request.setOccupation("Developer");
        request.setCompany("Test Company");
        request.setAnnualIncome(75000.0);
        return request;
    }

    /**
     * Helper method to create a sample user profile response
     */
    private UserProfileResponse createSampleUserProfileResponse() {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(1L);
        response.setKeycloakId("test-keycloak-id");
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setEmail("john.doe@example.com");
        response.setDateOfBirth(LocalDate.of(1990, 1, 1));
        response.setAge(35);
        response.setPhoneNumber("+1234567890");
        response.setAddress("123 Test St");
        response.setCity("Test City");
        response.setState("Test State");
        response.setZipCode("12345");
        response.setCountry("Test Country");
        response.setEmploymentStatus("EMPLOYED");
        response.setOccupation("Developer");
        response.setCompany("Test Company");
        response.setAnnualIncome(75000.0);
        response.setProfileCompleted(true);
        return response;
    }
}