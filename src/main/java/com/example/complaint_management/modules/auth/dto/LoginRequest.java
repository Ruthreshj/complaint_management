package com.example.complaint_management.modules.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
