package com.example.complaint_management.modules.complaint.dto;

import com.example.complaint_management.modules.complaint.domain.enums.ComplaintStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ResolutionLogResponse(
        @Schema(description = "Log entry id", example = "12")
        Long id,
        @Schema(description = "Complaint id", example = "5")
        Long complaintId,
        @Schema(description = "User id of the employee who updated the log", example = "3")
        Long updatedById,
        @Schema(description = "Resolution comment", example = "Issue reproduced and fix is in progress")
        String comment,
        @Schema(description = "Status captured in this log entry", example = "IN_PROGRESS")
        ComplaintStatus status,
        @Schema(description = "Log creation timestamp")
        LocalDateTime timestamp
) {
}