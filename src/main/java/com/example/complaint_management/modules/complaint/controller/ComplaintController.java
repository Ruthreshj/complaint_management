package com.example.complaint_management.modules.complaint.controller;

import com.example.complaint_management.modules.complaint.dto.ComplaintResponse;
import com.example.complaint_management.modules.complaint.dto.CreateComplaintRequest;
import com.example.complaint_management.modules.complaint.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/complaints")
@Tag(name = "Complaint Management", description = "Endpoints for raising and tracking complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    @Operation(summary = "Raise complaint")
    public ResponseEntity<?> createComplaint(@RequestBody CreateComplaintRequest request,
            Authentication authentication) {
        String email = extractAuthenticatedEmail(authentication);
        try {
            ComplaintResponse response = complaintService.createComplaint(email, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint by id")
    public ResponseEntity<?> getComplaintById(@PathVariable("id") Long complaintId) {
        try {
            return ResponseEntity.ok(complaintService.getComplaintById(complaintId));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/my")
    @Operation(summary = "Get complaints raised by current user")
    public ResponseEntity<?> getMyComplaints(Authentication authentication) {
        String email = extractAuthenticatedEmail(authentication);
        try {
            List<ComplaintResponse> complaints = complaintService.getMyComplaints(email);
            return ResponseEntity.ok(complaints);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all complaints")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    private String extractAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new NoSuchElementException("Authenticated user is required");
        }
        return authentication.getName();
    }
}
