package com.trustify.policy_service.service.impl;

import com.trustify.policy_service.model.InsurancePolicy;
import com.trustify.policy_service.model.UserProfile;
import com.trustify.policy_service.repository.InsurancePolicyRepository;
import com.trustify.policy_service.service.InsurancePolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InsurancePolicyService
 */
@Service
@Transactional
public class InsurancePolicyServiceImpl extends BaseServiceImpl<InsurancePolicy, Long, InsurancePolicyRepository> 
        implements InsurancePolicyService {

    public InsurancePolicyServiceImpl(InsurancePolicyRepository repository) {
        super(repository);
    }

    @Override
    public List<InsurancePolicy> findByUserProfile(UserProfile userProfile) {
        return repository.findByUserProfile(userProfile);
    }

    @Override
    public List<InsurancePolicy> findByKeycloakId(String keycloakId) {
        return repository.findByUserProfileKeycloakId(keycloakId);
    }

    @Override
    public List<InsurancePolicy> findActivePolicies() {
        LocalDate currentDate = LocalDate.now();
        // This method needs to be implemented via a custom query in the repository
        // For now, we'll filter the results in the service
        return repository.findAll().stream()
                .filter(policy -> policy.getStartDate().isBefore(currentDate) && 
                                policy.getEndDate().isAfter(currentDate))
                .toList();
    }

    @Override
    public List<InsurancePolicy> findByPolicyNumber(String policyNumber) {
        Optional<InsurancePolicy> policyOptional = repository.findByPolicyNumber(policyNumber);
        return policyOptional.map(List::of).orElse(List.of());
    }
}