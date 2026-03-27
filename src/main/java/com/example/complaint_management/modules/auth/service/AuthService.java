package com.example.complaint_management.modules.auth.service;

import com.example.complaint_management.modules.auth.domain.entity.AuthUser;
import com.example.complaint_management.modules.auth.domain.enums.AuthUserRole;
import com.example.complaint_management.modules.auth.dto.AuthResponse;
import com.example.complaint_management.modules.auth.dto.AuthTokenResponse;
import com.example.complaint_management.modules.auth.dto.LoginRequest;
import com.example.complaint_management.modules.auth.dto.RegisterRequest;
import com.example.complaint_management.modules.auth.repo.AuthUserRepository;
import com.example.complaint_management.modules.auth.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse register(RegisterRequest request, AuthUserRole role) {
        validateRegisterInput(request);

        if (authUserRepository.existsByEmail(request.email().trim())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        AuthUser user = new AuthUser();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);

        AuthUser saved = authUserRepository.save(user);
        return new AuthResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole(),
                "Registration successful"
        );
    }

    public AuthTokenResponse login(LoginRequest request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new IllegalArgumentException("Email and password are required");
        }

        AuthUser user = authUserRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());

        return new AuthTokenResponse(
            accessToken,
            "Bearer",
            jwtTokenProvider.getExpirationSeconds(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }

    private void validateRegisterInput(RegisterRequest request) {
        if (request == null || isBlank(request.name()) || isBlank(request.email()) || isBlank(request.password())) {
            throw new IllegalArgumentException("Name, email, and password are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
