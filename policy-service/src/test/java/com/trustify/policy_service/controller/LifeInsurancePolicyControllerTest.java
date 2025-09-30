package com.trustify.policy_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustify.policy_service.dto.request.LifeInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.LifeInsurancePolicyResponse;
import com.trustify.policy_service.service.LifeInsurancePolicyService;
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
 * Unit tests for the LifeInsurancePolicyController
 */
public class LifeInsurancePolicyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LifeInsurancePolicyService lifeInsurancePolicyService;

    @InjectMocks
    private LifeInsurancePolicyController lifeInsurancePolicyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lifeInsurancePolicyController).build();
        objectMapper.findAndRegisterModules(); // For proper handling of LocalDate
    }

    @Test
    @WithMockUser(roles = "user")
    public void testCreateLifeInsurancePolicy() throws Exception {
        // Prepare test data
        LifeInsurancePolicyRequest request = createSampleLifeInsurancePolicyRequest();
        LifeInsurancePolicyResponse response = createSampleLifeInsurancePolicyResponse();

        // Mock service call
        when(lifeInsurancePolicyService.createLifeInsurancePolicy(any(LifeInsurancePolicyRequest.class))).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(post("/api/policies/life")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.policyNumber").value(response.getPolicyNumber()));
    }

    @Test
    @WithMockUser(roles = "user")
    public void testUpdateLifeInsurancePolicy() throws Exception {
        // Prepare test data
        Long policyId = 1L;
        LifeInsurancePolicyRequest request = createSampleLifeInsurancePolicyRequest();
        LifeInsurancePolicyResponse response = createSampleLifeInsurancePolicyResponse();

        // Mock service call
        when(lifeInsurancePolicyService.updateLifeInsurancePolicy(eq(policyId), any(LifeInsurancePolicyRequest.class))).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(put("/api/policies/life/{id}", policyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.policyNumber").value(response.getPolicyNumber()));
    }

    @Test
    @WithMockUser(roles = "user")
    public void testGetLifeInsurancePolicy() throws Exception {
        // Prepare test data
        Long policyId = 1L;
        LifeInsurancePolicyResponse response = createSampleLifeInsurancePolicyResponse();

        // Mock service call
        when(lifeInsurancePolicyService.getLifeInsurancePolicy(policyId)).thenReturn(response);

        // Perform the request and validate
        mockMvc.perform(get("/api/policies/life/{id}", policyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.policyNumber").value(response.getPolicyNumber()));
    }

    @Test
    @WithMockUser(roles = "user")
    public void testGetCurrentUserLifeInsurancePolicies() throws Exception {
        // Prepare test data
        LifeInsurancePolicyResponse policy1 = createSampleLifeInsurancePolicyResponse();
        LifeInsurancePolicyResponse policy2 = createSampleLifeInsurancePolicyResponse();
        policy2.setId(2L);
        policy2.setPolicyNumber("LP-2023-002");
        List<LifeInsurancePolicyResponse> policies = Arrays.asList(policy1, policy2);

        // Mock service call
        when(lifeInsurancePolicyService.getCurrentUserLifeInsurancePolicies()).thenReturn(policies);

        // Perform the request and validate
        mockMvc.perform(get("/api/policies/life/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(policy1.getId()))
                .andExpect(jsonPath("$[0].policyNumber").value(policy1.getPolicyNumber()))
                .andExpect(jsonPath("$[1].id").value(policy2.getId()))
                .andExpect(jsonPath("$[1].policyNumber").value(policy2.getPolicyNumber()));
    }

    @Test
    @WithMockUser(roles = "admin")
    public void testGetAllLifeInsurancePolicies() throws Exception {
        // Prepare test data
        LifeInsurancePolicyResponse policy1 = createSampleLifeInsurancePolicyResponse();
        LifeInsurancePolicyResponse policy2 = createSampleLifeInsurancePolicyResponse();
        policy2.setId(2L);
        policy2.setPolicyNumber("LP-2023-002");
        List<LifeInsurancePolicyResponse> policies = Arrays.asList(policy1, policy2);

        // Mock service call
        when(lifeInsurancePolicyService.getAllLifeInsurancePolicies()).thenReturn(policies);

        // Perform the request and validate
        mockMvc.perform(get("/api/policies/life"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(policy1.getId()))
                .andExpect(jsonPath("$[0].policyNumber").value(policy1.getPolicyNumber()))
                .andExpect(jsonPath("$[1].id").value(policy2.getId()))
                .andExpect(jsonPath("$[1].policyNumber").value(policy2.getPolicyNumber()));
    }

    @Test
    @WithMockUser(roles = "admin")
    public void testDeleteLifeInsurancePolicy() throws Exception {
        // Prepare test data
        Long policyId = 1L;

        // Mock service call
        doNothing().when(lifeInsurancePolicyService).deleteLifeInsurancePolicy(policyId);

        // Perform the request and validate
        mockMvc.perform(delete("/api/policies/life/{id}", policyId))
                .andExpect(status().isNoContent());
    }

    /**
     * Helper method to create a sample life insurance policy request
     */
    private LifeInsurancePolicyRequest createSampleLifeInsurancePolicyRequest() {
        // We'll use constructor with basic fields needed for the test
        LifeInsurancePolicyRequest request = new LifeInsurancePolicyRequest();
        
        // Using direct field access since we're having issues with setters and builders
        try {
            // Set InsurancePolicyRequest fields (parent class)
            java.lang.reflect.Field coverageAmountField = request.getClass().getSuperclass().getDeclaredField("coverageAmount");
            coverageAmountField.setAccessible(true);
            coverageAmountField.set(request, new java.math.BigDecimal("500000.00"));
            
            java.lang.reflect.Field startDateField = request.getClass().getSuperclass().getDeclaredField("startDate");
            startDateField.setAccessible(true);
            startDateField.set(request, LocalDate.now());
            
            java.lang.reflect.Field endDateField = request.getClass().getSuperclass().getDeclaredField("endDate");
            endDateField.setAccessible(true);
            endDateField.set(request, LocalDate.now().plusYears(20));
            
            java.lang.reflect.Field paymentFrequencyField = request.getClass().getSuperclass().getDeclaredField("paymentFrequency");
            paymentFrequencyField.setAccessible(true);
            paymentFrequencyField.set(request, "MONTHLY");
            
            // Set LifeInsurancePolicyRequest fields (child class)
            java.lang.reflect.Field policyTypeField = request.getClass().getDeclaredField("policyType");
            policyTypeField.setAccessible(true);
            policyTypeField.set(request, "TERM");
            
            java.lang.reflect.Field includesCriticalIllnessField = request.getClass().getDeclaredField("includesCriticalIllness");
            includesCriticalIllnessField.setAccessible(true);
            includesCriticalIllnessField.set(request, Boolean.TRUE);
            
            java.lang.reflect.Field includesDisabilityBenefitField = request.getClass().getDeclaredField("includesDisabilityBenefit");
            includesDisabilityBenefitField.setAccessible(true);
            includesDisabilityBenefitField.set(request, Boolean.FALSE);
            
            java.lang.reflect.Field beneficiaryNameField = request.getClass().getDeclaredField("beneficiaryName");
            beneficiaryNameField.setAccessible(true);
            beneficiaryNameField.set(request, "Jane Doe");
            
            java.lang.reflect.Field beneficiaryRelationshipField = request.getClass().getDeclaredField("beneficiaryRelationship");
            beneficiaryRelationshipField.setAccessible(true);
            beneficiaryRelationshipField.set(request, "Spouse");
        } catch (Exception e) {
            System.err.println("Error setting up test request: " + e.getMessage());
        }
        
        return request;
    }

    /**
     * Helper method to create a sample life insurance policy response
     */
    private LifeInsurancePolicyResponse createSampleLifeInsurancePolicyResponse() {
        LifeInsurancePolicyResponse response = new LifeInsurancePolicyResponse();
        // Use reflection to set fields since we're seeing compilation issues with both setters and builders
        try {
            java.lang.reflect.Field idField = LifeInsurancePolicyResponse.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(response, 1L);
            
            java.lang.reflect.Field policyNumberField = LifeInsurancePolicyResponse.class.getSuperclass().getDeclaredField("policyNumber");
            policyNumberField.setAccessible(true);
            policyNumberField.set(response, "LP-2023-001");
            
            java.lang.reflect.Field coverageAmountField = LifeInsurancePolicyResponse.class.getSuperclass().getDeclaredField("coverageAmount");
            coverageAmountField.setAccessible(true);
            coverageAmountField.set(response, new java.math.BigDecimal("500000.00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}