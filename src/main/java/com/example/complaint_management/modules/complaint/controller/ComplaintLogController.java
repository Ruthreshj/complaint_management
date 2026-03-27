package com.example.complaint_management.modules.complaint.controller;

import com.example.complaint_management.modules.auth.dto.ApiErrorResponse;
import com.example.complaint_management.modules.complaint.dto.CreateResolutionLogRequest;
import com.example.complaint_management.modules.complaint.dto.ResolutionLogResponse;
import com.example.complaint_management.modules.complaint.service.ComplaintLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Complaint Logs", description = "Endpoints for complaint resolution logs")
@SecurityRequirement(name = "bearerAuth")
public class ComplaintLogController {

    private final ComplaintLogService complaintLogService;

    public ComplaintLogController(ComplaintLogService complaintLogService) {
        this.complaintLogService = complaintLogService;
    }

    @PostMapping("/employee/complaints/{id}/logs")
    @PreAuthorize("hasRole('EMPLOYEE')")
        @Operation(
            summary = "Create complaint log as employee",
            description = "Adds a new resolution log entry for a complaint assigned to the authenticated employee and updates the complaint status."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Log created",
                    content = @Content(schema = @Schema(implementation = ResolutionLogResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Complaint not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<?> createEmployeeComplaintLog(
            @Parameter(description = "Complaint id", example = "10")
            @PathVariable("id") Long complaintId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Resolution log payload",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateResolutionLogRequest.class))
            )
            @RequestBody CreateResolutionLogRequest request,
            Authentication authentication
    ) {
        try {
            ResolutionLogResponse response = complaintLogService.createEmployeeLog(
                    complaintId,
                    extractAuthenticatedEmail(authentication),
                    request
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiErrorResponse(ex.getMessage()));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/complaints/{id}/logs")
        @Operation(
            summary = "Get complaint logs",
            description = "Fetches all resolution logs for a complaint in reverse chronological order."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logs fetched",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResolutionLogResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Complaint not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
        public ResponseEntity<?> getComplaintLogs(
            @Parameter(description = "Complaint id", example = "10")
            @PathVariable("id") Long complaintId
        ) {
        try {
            List<ResolutionLogResponse> logs = complaintLogService.getLogsByComplaintId(complaintId);
            return ResponseEntity.ok(logs);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(ex.getMessage()));
        }
    }

    private String extractAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new NoSuchElementException("Authenticated user is required");
        }
        return authentication.getName();
    }
}