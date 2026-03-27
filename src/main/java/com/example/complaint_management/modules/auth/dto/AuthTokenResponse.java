package com.example.complaint_management.modules.auth.dto;

import com.example.complaint_management.modules.auth.domain.enums.AuthUserRole;

public record AuthTokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        Long id,
        String name,
        String email,
        AuthUserRole role,
        String message
) {
}
