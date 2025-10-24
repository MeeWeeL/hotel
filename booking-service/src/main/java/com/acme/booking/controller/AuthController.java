package com.acme.booking.controller;

import com.acme.booking.dto.AuthDtos.LoginRequest;
import com.acme.booking.dto.AuthDtos.RegisterRequest;
import com.acme.booking.dto.AuthDtos.TokenResponse;
import com.acme.booking.model.Role;
import com.acme.booking.service.JwtService;
import com.acme.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService users;
    private final JwtService jwt;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterRequest req) {
        var u = users.register(req.username(), req.password(), Role.USER);
        var token = jwt.issue(u.getUsername(), u.getRole());
        return new TokenResponse(token, "Bearer", 3600);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        var u = users.byUsername(req.username()).orElseThrow(() -> new RuntimeException("Bad credentials"));
        if (!encoder.matches(req.password(), u.getPassword())) throw new RuntimeException("Bad credentials");
        var token = jwt.issue(u.getUsername(), u.getRole());
        return new TokenResponse(token, "Bearer", 3600);
    }
}
