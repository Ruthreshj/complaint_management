package com.example.complaint_management.modules.work.controller;

import com.example.complaint_management.modules.auth.dto.ApiErrorResponse;
import com.example.complaint_management.modules.work.dto.WorkComplaintResponse;
import com.example.complaint_management.modules.work.dto.WorkUpdateComplaintStatusRequest;
import com.example.complaint_management.modules.work.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee/complaints")
@PreAuthorize("hasRole('EMPLOYEE')")
@Tag(name = "Employee Work", description = "Employee complaint work management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping
    @Operation(summary = "List assigned complaints", description = "Gets all complaints assigned to the authenticated employee")
    @ApiResponse(responseCode = "200", description = "Complaints fetched")
    public ResponseEntity<List<WorkComplaintResponse>> getAssignedComplaints(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(workService.getAssignedComplaints(email));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update complaint status", description = "Only the assigned employee can update. Cannot close without resolution.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated", content = @Content(schema = @Schema(implementation = WorkComplaintResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody WorkUpdateComplaintStatusRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        try {
            WorkComplaintResponse response = workService.updateComplaintStatus(id, request, email);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(ex.getMessage()));
        }
    }
}
