package com.claims.claims_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Claim extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String claimNumber;
    
    @Column(nullable = false)
    private String policyNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType policyType;
    
    @Column(nullable = false)
    private String keycloakUserId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType claimType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;
    
    @Column(nullable = false)
    private LocalDate incidentDate;
    
    @Column(nullable = false)
    private LocalDateTime submittedDate;
    
    private LocalDate approvedDate;
    private LocalDate rejectedDate;
    private LocalDate settledDate;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal claimedAmount;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal approvedAmount;
    
    @Column(nullable = false, length = 2000)
    private String description;
    
    @Column(length = 500)
    private String incidentLocation;
    
    @Column(length = 1000)
    private String rejectionReason;
    
    @Column(length = 1000)
    private String adminNotes;
    
    private String documentsPath;
    
    private String reviewedBy;
    
    @Enumerated(EnumType.STRING)
    private Severity severity;
    
    public enum ClaimStatus {
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        SETTLED,
        CANCELLED
    }
    
    public enum ClaimType {
        // Life Insurance Claims
        DEATH_CLAIM,
        CRITICAL_ILLNESS_CLAIM,
        DISABILITY_CLAIM,
        
        // Car Insurance Claims
        ACCIDENT_CLAIM,
        THEFT_CLAIM,
        VANDALISM_CLAIM,
        NATURAL_DISASTER_CAR_CLAIM,
        
        // House Insurance Claims
        FIRE_DAMAGE_CLAIM,
        WATER_DAMAGE_CLAIM,
        THEFT_HOME_CLAIM,
        NATURAL_DISASTER_HOME_CLAIM,
        LIABILITY_CLAIM,
        
        // General
        OTHER
    }
    
    public enum PolicyType {
        LIFE,
        CAR,
        HOUSE
    }
    
    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    @PrePersist
    protected void onCreate() {
        if (submittedDate == null) {
            submittedDate = LocalDateTime.now();
        }
        if (status == null) {
            status = ClaimStatus.SUBMITTED;
        }
        if (severity == null) {
            severity = Severity.MEDIUM;
        }
    }
}
