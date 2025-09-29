package com.trustify.policy_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String keycloakId;
    
    private String firstName;
    private String lastName;
    private String email;
    
    private LocalDate dateOfBirth;
    private Integer age;
    
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;
    
    private String occupation;
    private String company;
    private Double annualIncome;
    
    private Boolean profileCompleted = false;
    
    public enum EmploymentStatus {
        EMPLOYED,
        SELF_EMPLOYED,
        UNEMPLOYED,
        STUDENT,
        RETIRED
    }
}