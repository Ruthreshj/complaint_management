package com.example.complaint_management.modules.work.dto;

import com.example.complaint_management.modules.work.domain.enums.WorkComplaintStatus;

public record WorkUpdateComplaintStatusRequest(
        WorkComplaintStatus status,
        String resolutionComment) {
}
