package com.trustify.policy_service.service.impl;

import com.trustify.policy_service.dto.request.CarInsurancePolicyRequest;
import com.trustify.policy_service.dto.response.CarInsurancePolicyResponse;
import com.trustify.policy_service.exception.ResourceNotFoundException;
import com.trustify.policy_service.mapper.CarInsurancePolicyMapper;
import com.trustify.policy_service.model.CarInsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import com.trustify.policy_service.repository.CarInsurancePolicyRepository;
import com.trustify.policy_service.repository.UserProfileRepository;
import com.trustify.policy_service.service.CarInsurancePolicyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of CarInsurancePolicyService
 */
@Service
@Transactional
public class CarInsurancePolicyServiceImpl extends BaseServiceImpl<CarInsurancePolicy, Long, CarInsurancePolicyRepository>
        implements CarInsurancePolicyService {

    private final CarInsurancePolicyMapper carInsurancePolicyMapper;
    private final UserProfileRepository userProfileRepository;

    public CarInsurancePolicyServiceImpl(
            CarInsurancePolicyRepository repository,
            CarInsurancePolicyMapper carInsurancePolicyMapper,
            UserProfileRepository userProfileRepository) {
        super(repository);
        this.carInsurancePolicyMapper = carInsurancePolicyMapper;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public List<CarInsurancePolicy> findByUserProfile(UserProfile userProfile) {
        return repository.findByUserProfile(userProfile);
    }

    @Override
    public List<CarInsurancePolicy> findByKeycloakId(String keycloakId) {
        return repository.findByUserProfileKeycloakId(keycloakId);
    }
    
    @Override
    public List<CarInsurancePolicy> findByVehicleVIN(String vin) {
        return repository.findByVehicleVIN(vin);
    }
    
    @Override
    public List<CarInsurancePolicy> findByLicensePlate(String licensePlate) {
        return repository.findByLicensePlate(licensePlate);
    }

    @Override
    public CarInsurancePolicyResponse createCarInsurancePolicy(CarInsurancePolicyRequest request) {
        CarInsurancePolicy policy = carInsurancePolicyMapper.toEntity(request);
        
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
        policy.setPolicyNumber("CAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        CarInsurancePolicy savedPolicy = repository.save(policy);
        return carInsurancePolicyMapper.toDto(savedPolicy);
    }

    @Override
    public CarInsurancePolicyResponse updateCarInsurancePolicy(Long id, CarInsurancePolicyRequest request) {
        CarInsurancePolicy existingPolicy = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car insurance policy not found with ID: " + id));
        
        // Update policy fields
        CarInsurancePolicy updatedPolicy = carInsurancePolicyMapper.toEntity(request);
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
        
        CarInsurancePolicy savedPolicy = repository.save(updatedPolicy);
        return carInsurancePolicyMapper.toDto(savedPolicy);
    }

    @Override
    public CarInsurancePolicyResponse getCarInsurancePolicy(Long id) {
        CarInsurancePolicy policy = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car insurance policy not found with ID: " + id));
        return carInsurancePolicyMapper.toDto(policy);
    }

    @Override
    public List<CarInsurancePolicyResponse> getAllCarInsurancePolicies() {
        return repository.findAll().stream()
                .map(carInsurancePolicyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarInsurancePolicyResponse> getCurrentUserCarInsurancePolicies() {
        String keycloakId = getCurrentUserKeycloakId();
        return repository.findByUserProfileKeycloakId(keycloakId).stream()
                .map(carInsurancePolicyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCarInsurancePolicy(Long id) {
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
            CarInsurancePolicy policy = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car insurance policy not found with ID: " + id));
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