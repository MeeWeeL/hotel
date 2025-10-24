package com.acme.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record TokenResponse(
            String access_token,
            String token_type,
            long expires_in
    ) {
    }
}
