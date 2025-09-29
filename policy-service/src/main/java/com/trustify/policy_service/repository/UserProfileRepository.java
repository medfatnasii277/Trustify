package com.trustify.policy_service.repository;

import com.trustify.policy_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByKeycloakId(String keycloakId);
    
    Optional<UserProfile> findByEmail(String email);
    
    boolean existsByKeycloakId(String keycloakId);
}