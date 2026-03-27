package com.example.complaint_management.modules.manager.controller;

import com.example.complaint_management.modules.auth.dto.ApiErrorResponse;
import com.example.complaint_management.modules.manager.dto.AssignComplaintRequest;
import com.example.complaint_management.modules.manager.dto.ManagerAssignResponse;
import com.example.complaint_management.modules.manager.dto.ManagerComplaintResponse;
import com.example.complaint_management.modules.manager.dto.ManagerEmployeeResponse;
import com.example.complaint_management.modules.manager.service.ManagerComplaintService;
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
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Manager", description = "Manager-only complaint management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ManagerComplaintController {

    private final ManagerComplaintService managerComplaintService;

    public ManagerComplaintController(ManagerComplaintService managerComplaintService) {
        this.managerComplaintService = managerComplaintService;
    }

    @PutMapping("/complaints/{id}/assign")
    @Operation(summary = "Assign complaint", description = "Assigns a complaint to an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint assigned",
                    content = @Content(schema = @Schema(implementation = ManagerAssignResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<?> assignComplaint(
            @PathVariable Long id,
            @RequestBody AssignComplaintRequest request,
            Authentication authentication
    ) {
        try {
            ManagerAssignResponse response = managerComplaintService.assignComplaint(id, request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/complaints")
    @Operation(summary = "List complaints", description = "Gets all complaints for manager overview")
    @ApiResponse(responseCode = "200", description = "Complaints fetched")
    public ResponseEntity<List<ManagerComplaintResponse>> getComplaints() {
        return ResponseEntity.ok(managerComplaintService.getAllComplaints());
    }

    @GetMapping("/employees")
    @Operation(summary = "List employees", description = "Gets all employees available for assignment")
    @ApiResponse(responseCode = "200", description = "Employees fetched")
    public ResponseEntity<List<ManagerEmployeeResponse>> getEmployees() {
        return ResponseEntity.ok(managerComplaintService.getEmployees());
    }
}
