package com.example.complaint_management.modules.auth.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
