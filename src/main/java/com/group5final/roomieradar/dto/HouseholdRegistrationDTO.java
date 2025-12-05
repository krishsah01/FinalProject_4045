package com.group5final.roomieradar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HouseholdRegistrationDTO {
    @NotBlank(message = "Household name is required")
    @Size(min = 3, max = 50, message = "Household name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
