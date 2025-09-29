package com.trustify.policy_service.service.impl;

import com.trustify.policy_service.dto.request.LifeInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.LifeInsurancePolicyResponse;
import com.trustify.policy_service.exception.ResourceNotFoundException;
import com.trustify.policy_service.mapper.LifeInsurancePolicyMapper;
import com.trustify.policy_service.model.LifeInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import com.trustify.policy_service.repository.LifeInsurancePolicyRepository;
import com.trustify.policy_service.repository.UserProfileRepository;
import com.trustify.policy_service.service.LifeInsurancePolicyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of LifeInsurancePolicyService
 */
@Service
@Transactional
public class LifeInsurancePolicyServiceImpl extends BaseServiceImpl<LifeInsurancePolicy, Long, LifeInsurancePolicyRepository>
        implements LifeInsurancePolicyService {

    private final LifeInsurancePolicyMapper lifeInsurancePolicyMapper;
    private final UserProfileRepository userProfileRepository;

    public LifeInsurancePolicyServiceImpl(
            LifeInsurancePolicyRepository repository,
            LifeInsurancePolicyMapper lifeInsurancePolicyMapper,
            UserProfileRepository userProfileRepository) {
        super(repository);
        this.lifeInsurancePolicyMapper = lifeInsurancePolicyMapper;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public List<LifeInsurancePolicy> findByUserProfile(UserProfile userProfile) {
        return repository.findByUserProfile(userProfile);
    }

    @Override
    public List<LifeInsurancePolicy> findByKeycloakId(String keycloakId) {
        return repository.findByUserProfileKeycloakId(keycloakId);
    }

    @Override
    public LifeInsurancePolicyResponse createLifeInsurancePolicy(LifeInsurancePolicyRequest request) {
        LifeInsurancePolicy policy = lifeInsurancePolicyMapper.toEntity(request);
        
        // Generate policy number if not provided
        if (policy.getPolicyNumber() == null || policy.getPolicyNumber().isEmpty()) {
            policy.setPolicyNumber("LIFE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        // Set user profile from authenticated user if not provided
        if (policy.getUserProfile() == null && request.getUserProfileId() == null) {
            String keycloakId = getCurrentUserKeycloakId();
            UserProfile userProfile = userProfileRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new ResourceNotFoundException("User profile not found for current user"));
            policy.setUserProfile(userProfile);
        } else if (request.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(request.getUserProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + request.getUserProfileId()));
            policy.setUserProfile(userProfile);
        }
        
        LifeInsurancePolicy savedPolicy = repository.save(policy);
        return lifeInsurancePolicyMapper.toResponse(savedPolicy);
    }

    @Override
    public LifeInsurancePolicyResponse updateLifeInsurancePolicy(Long id, LifeInsurancePolicyRequest request) {
        LifeInsurancePolicy existingPolicy = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Life insurance policy not found with ID: " + id));
        
        // Update policy fields
        lifeInsurancePolicyMapper.updateEntityFromRequest(request, existingPolicy);
        
        // If user profile ID is provided, update the user profile
        if (request.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(request.getUserProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + request.getUserProfileId()));
            existingPolicy.setUserProfile(userProfile);
        }
        
        LifeInsurancePolicy updatedPolicy = repository.save(existingPolicy);
        return lifeInsurancePolicyMapper.toResponse(updatedPolicy);
    }

    @Override
    public LifeInsurancePolicyResponse getLifeInsurancePolicy(Long id) {
        LifeInsurancePolicy policy = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Life insurance policy not found with ID: " + id));
        return lifeInsurancePolicyMapper.toResponse(policy);
    }

    @Override
    public List<LifeInsurancePolicyResponse> getAllLifeInsurancePolicies() {
        return repository.findAll().stream()
                .map(lifeInsurancePolicyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LifeInsurancePolicyResponse> getCurrentUserLifeInsurancePolicies() {
        String keycloakId = getCurrentUserKeycloakId();
        return repository.findByUserProfileKeycloakId(keycloakId).stream()
                .map(lifeInsurancePolicyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLifeInsurancePolicy(Long id) {
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