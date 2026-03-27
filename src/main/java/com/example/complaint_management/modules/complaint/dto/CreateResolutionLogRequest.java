package com.example.complaint_management.modules.complaint.dto;

import com.example.complaint_management.modules.complaint.domain.enums.ComplaintStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateResolutionLogRequest(
        @Schema(description = "Resolution update comment", example = "Checked logs and restarted service")
        String comment,
        @Schema(description = "Updated complaint status", example = "IN_PROGRESS")
        ComplaintStatus status
) {
}