package com.example.complaint_management.modules.auth.controller;

import com.example.complaint_management.modules.auth.domain.enums.AuthUserRole;
import com.example.complaint_management.modules.auth.dto.ApiErrorResponse;
import com.example.complaint_management.modules.auth.dto.AuthResponse;
import com.example.complaint_management.modules.auth.dto.AuthTokenResponse;
import com.example.complaint_management.modules.auth.dto.LoginRequest;
import com.example.complaint_management.modules.auth.dto.RegisterRequest;
import com.example.complaint_management.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/manager")
        @Operation(summary = "Register manager", description = "Creates a new manager account")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Manager registered",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
        })
    public ResponseEntity<?> registerManager(@RequestBody RegisterRequest request) {
        return registerByRole(request, AuthUserRole.MANAGER);
    }

    @PostMapping("/register/employee")
        @Operation(summary = "Register employee", description = "Creates a new employee account")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee registered",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
        })
    public ResponseEntity<?> registerEmployee(@RequestBody RegisterRequest request) {
        return registerByRole(request, AuthUserRole.EMPLOYEE);
    }

    @PostMapping("/register/user")
        @Operation(summary = "Register user", description = "Creates a new user account")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
        })
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return registerByRole(request, AuthUserRole.USER);
    }

    @PostMapping("/login")
        @Operation(summary = "Login", description = "Authenticates a user and returns a JWT access token")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
        })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthTokenResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    private ResponseEntity<?> registerByRole(RegisterRequest request, AuthUserRole role) {
        try {
            AuthResponse response = authService.register(request, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
