package com.example.complaint_management.modules.complaint.dto;

import com.example.complaint_management.modules.complaint.domain.enums.ComplaintPriority;
import com.example.complaint_management.modules.complaint.domain.enums.ComplaintStatus;
import java.time.LocalDateTime;

public record ComplaintResponse(
        Long id,
        String title,
        String description,
        ComplaintStatus status,
        ComplaintPriority priority,
        Long createdById,
        Long assignedToId,
        Long assignedById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
