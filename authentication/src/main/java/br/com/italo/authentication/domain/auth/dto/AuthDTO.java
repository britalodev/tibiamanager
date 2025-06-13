package br.com.italo.authentication.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public sealed interface AuthDTO {
    record LoginRequest(
            @NotBlank(message = "Username cannot be blank")
            String username,
            @NotBlank(message = "Password cannot be blank")
            String password
    ) implements AuthDTO {}

    record RegisterRequest(
            @NotBlank(message = "Username cannot be blank")
            @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
            String username,
            @NotBlank(message = "Email cannot be blank")
            @Email(message = "Invalid email format")
            String email,
            @NotBlank(message = "Password cannot be blank")
            @Size(min = 8, message = "Password must be at least 8 characters long")
            String password
    ) implements AuthDTO {}

    record RegisterResponse(
            String message,
            String username,
            String email
    ) implements AuthDTO {}

    record AuthResponse(
            String accessToken,
            String refreshToken
    ) implements AuthDTO {}

    record RefreshTokenRequest(
            @NotBlank(message = "Refresh token cannot be blank")
            String refreshToken
    ) implements AuthDTO {}

    record UserDTO(
            String id,
            String username,
            String email,
            Set<String> roles
    ) implements AuthDTO {}
}
