package com.xpguardian.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @Email(message = "Email should be valid.")
    @NotEmpty(message = "Email cannot be empty.")
    private String email;

    @NotEmpty(message = "Password cannot be empty.")
    private String password;
}