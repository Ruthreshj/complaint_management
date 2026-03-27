package com.example.complaint_management.modules.work.dto;

import com.example.complaint_management.modules.work.domain.enums.WorkComplaintPriority;
import com.example.complaint_management.modules.work.domain.enums.WorkComplaintStatus;
import java.time.LocalDateTime;

public record WorkComplaintResponse(
        Long id,
        String title,
        String description,
        WorkComplaintStatus status,
        WorkComplaintPriority priority,
        Long createdById,
        Long assignedToId,
        Long assignedById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
