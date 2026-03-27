package com.example.complaint_management.modules.manager.dto;

import com.example.complaint_management.modules.manager.domain.enums.ManagerComplaintPriority;
import com.example.complaint_management.modules.manager.domain.enums.ManagerComplaintStatus;
import java.time.LocalDateTime;

public record ManagerComplaintResponse(
        Long id,
        String title,
        String description,
        ManagerComplaintStatus status,
        ManagerComplaintPriority priority,
        Long createdById,
        Long assignedToId,
        Long assignedById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
