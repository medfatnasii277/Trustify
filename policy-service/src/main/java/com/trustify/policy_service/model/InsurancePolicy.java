package com.trustify.policy_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "policy_type")
@Table(name = "insurance_policies")
@Getter
@Setter
public abstract class InsurancePolicy extends BaseEntity {
    
    @Column(unique = true)
    private String policyNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    private PolicyStatus status;
    
    private BigDecimal premiumAmount;
    private PaymentFrequency paymentFrequency;
    
    private BigDecimal coverageAmount;
    private String description;
    
    public enum PolicyStatus {
        ACTIVE,
        PENDING,
        CANCELLED,
        EXPIRED
    }
    
    public enum PaymentFrequency {
        MONTHLY,
        QUARTERLY,
        SEMI_ANNUALLY,
        ANNUALLY
    }
}