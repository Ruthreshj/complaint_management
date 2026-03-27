package com.example.complaint_management.modules.complaint.dto;

import com.example.complaint_management.modules.complaint.domain.enums.ComplaintPriority;

public record CreateComplaintRequest(
        String title,
        String description,
        ComplaintPriority priority) {
}
