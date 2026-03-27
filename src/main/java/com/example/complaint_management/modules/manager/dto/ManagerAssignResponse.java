package com.example.complaint_management.modules.manager.dto;

import com.example.complaint_management.modules.manager.domain.enums.ManagerComplaintStatus;

public record ManagerAssignResponse(
        Long complaintId,
        Long assignedToEmployeeId,
        Long assignedByManagerId,
        ManagerComplaintStatus status,
        String message
) {
}
