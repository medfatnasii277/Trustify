package com.trustify.policy_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", 
            message = "Phone number is not valid")
    private String phoneNumber;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Zip code format is invalid")
    private String zipCode;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotNull(message = "Employment status is required")
    private String employmentStatus;
    
    private String occupation;
    private String company;
    
    @PositiveOrZero(message = "Annual income cannot be negative")
    private Double annualIncome;
}