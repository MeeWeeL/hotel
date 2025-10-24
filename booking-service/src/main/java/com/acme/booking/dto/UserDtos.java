package com.acme.booking.dto;

import com.acme.booking.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDtos {
    public record CreateUser(
            @NotBlank String username,
            @NotBlank String password,
            @NotNull Role role
    ) {

    }

    public record UpdateUser(
            @NotNull Long id,
            String password,
            Role role
    ) {
    }
}
