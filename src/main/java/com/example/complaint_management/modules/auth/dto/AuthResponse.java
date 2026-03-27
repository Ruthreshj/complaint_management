package com.example.complaint_management.modules.auth.dto;

import com.example.complaint_management.modules.auth.domain.enums.AuthUserRole;

public record AuthResponse(
        Long id,
        String name,
        String email,
        AuthUserRole role,
        String message
) {
}
