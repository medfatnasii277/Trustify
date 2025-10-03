package com.trustify.policy_service.service.impl;

import com.trustify.policy_service.dto.request.HouseInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.HouseInsurancePolicyResponse;
import com.trustify.policy_service.exception.ResourceNotFoundException;
import com.trustify.policy_service.mapper.HouseInsurancePolicyMapper;
import com.trustify.policy_service.model.HouseInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import com.trustify.policy_service.repository.HouseInsurancePolicyRepository;
import com.trustify.policy_service.repository.UserProfileRepository;
import com.trustify.policy_service.service.HouseInsurancePolicyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of HouseInsurancePolicyService
 */
@Service
@Transactional
public class HouseInsurancePolicyServiceImpl extends BaseServiceImpl<HouseInsurancePolicy, Long, HouseInsurancePolicyRepository>
        implements HouseInsurancePolicyService {

    private final HouseInsurancePolicyMapper houseInsurancePolicyMapper;
    private final UserProfileRepository userProfileRepository;

    public HouseInsurancePolicyServiceImpl(
            HouseInsurancePolicyRepository repository,
            HouseInsurancePolicyMapper houseInsurancePolicyMapper,
            UserProfileRepository userProfileRepository) {
        super(repository);
        this.houseInsurancePolicyMapper = houseInsurancePolicyMapper;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public List<HouseInsurancePolicy> findByUserProfile(UserProfile userProfile) {
        return repository.findByUserProfile(userProfile);
    }

    @Override
    public List<HouseInsurancePolicy> findByKeycloakId(String keycloakId) {
        return repository.findByUserProfileKeycloakId(keycloakId);
    }
    
    @Override
    public List<HouseInsurancePolicy> findByPropertyAddress(String propertyAddress) {
        return repository.findByPropertyAddress(propertyAddress);
    }
    
    @Override
    public List<HouseInsurancePolicy> findByPostalCode(String postalCode) {
        // This method is kept for interface compatibility
        // You can implement custom query with LIKE operator if needed
        return repository.findByPropertyAddress("%" + postalCode + "%");
    }

    @Override
    public HouseInsurancePolicyResponse createHouseInsurancePolicy(HouseInsurancePolicyRequest request) {
        HouseInsurancePolicy policy = houseInsurancePolicyMapper.toEntity(request);
        
        // Set user profile if provided with ID
        if (request.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(request.getUserProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + request.getUserProfileId()));
            policy.setUserProfile(userProfile);
        } else {
            // Set user profile from authenticated user
            String keycloakId = getCurrentUserKeycloakId();
            UserProfile userProfile = userProfileRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new ResourceNotFoundException("User profile not found for current user"));
            policy.setUserProfile(userProfile);
        }
        
        // Generate policy number
        policy.setPolicyNumber("HOUSE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        HouseInsurancePolicy savedPolicy = repository.save(policy);
        return houseInsurancePolicyMapper.toDto(savedPolicy);
    }

    @Override
    public HouseInsurancePolicyResponse updateHouseInsurancePolicy(Long id, HouseInsurancePolicyRequest request) {
        HouseInsurancePolicy existingPolicy = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("House insurance policy not found with ID: " + id));
        
        // Update policy fields
        HouseInsurancePolicy updatedPolicy = houseInsurancePolicyMapper.toEntity(request);
        updatedPolicy.setId(existingPolicy.getId());
        updatedPolicy.setPolicyNumber(existingPolicy.getPolicyNumber());
        updatedPolicy.setCreatedAt(existingPolicy.getCreatedAt());
        updatedPolicy.setCreatedBy(existingPolicy.getCreatedBy());
        updatedPolicy.setUserProfile(existingPolicy.getUserProfile());
        
        // If user profile ID is provided, update the user profile
        if (request.getUserProfileId() != null) {
            UserProfile userProfile = userProfileRepository.findById(request.getUserProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + request.getUserProfileId()));
            updatedPolicy.setUserProfile(userProfile);
        }
        
        HouseInsurancePolicy savedPolicy = repository.save(updatedPolicy);
        return houseInsurancePolicyMapper.toDto(savedPolicy);
    }

    @Override
    public HouseInsurancePolicyResponse getHouseInsurancePolicy(Long id) {
        HouseInsurancePolicy policy = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("House insurance policy not found with ID: " + id));
        return houseInsurancePolicyMapper.toDto(policy);
    }

    @Override
    public List<HouseInsurancePolicyResponse> getAllHouseInsurancePolicies() {
        return repository.findAll().stream()
                .map(houseInsurancePolicyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseInsurancePolicyResponse> getCurrentUserHouseInsurancePolicies() {
        String keycloakId = getCurrentUserKeycloakId();
        return repository.findByUserProfileKeycloakId(keycloakId).stream()
                .map(houseInsurancePolicyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHouseInsurancePolicy(Long id) {
        repository.deleteById(id);
    }
    
    /**
     * Check if the current user is the owner of the policy
     *
     * @param id the policy ID
     * @return true if current user is the owner, false otherwise
     */
    public boolean isOwner(Long id) {
        try {
            String keycloakId = getCurrentUserKeycloakId();
            HouseInsurancePolicy policy = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("House insurance policy not found with ID: " + id));
            return policy.getUserProfile().getKeycloakId().equals(keycloakId);
        } catch (Exception e) {
            return false;
        }
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