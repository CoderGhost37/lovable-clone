package com.kushagramathur.lovable_clone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email@NotBlank String email,
        @Size(min = 3, max = 30) String name,
        @Size(min = 5) String password
) {
}
