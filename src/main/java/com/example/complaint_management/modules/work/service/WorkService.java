package com.example.complaint_management.modules.work.service;

import com.example.complaint_management.modules.work.domain.entity.WorkComplaint;
import com.example.complaint_management.modules.work.domain.entity.WorkResolutionLog;
import com.example.complaint_management.modules.work.domain.entity.WorkUser;
import com.example.complaint_management.modules.work.domain.enums.WorkComplaintStatus;
import com.example.complaint_management.modules.work.domain.enums.WorkUserRole;
import com.example.complaint_management.modules.work.dto.WorkComplaintResponse;
import com.example.complaint_management.modules.work.dto.WorkUpdateComplaintStatusRequest;
import com.example.complaint_management.modules.work.repo.WorkComplaintRepository;
import com.example.complaint_management.modules.work.repo.WorkResolutionLogRepository;
import com.example.complaint_management.modules.work.repo.WorkUserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkService {

    private final WorkComplaintRepository workComplaintRepository;
    private final WorkUserRepository workUserRepository;
    private final WorkResolutionLogRepository workResolutionLogRepository;

    public WorkService(WorkComplaintRepository workComplaintRepository,
            WorkUserRepository workUserRepository,
            WorkResolutionLogRepository workResolutionLogRepository) {
        this.workComplaintRepository = workComplaintRepository;
        this.workUserRepository = workUserRepository;
        this.workResolutionLogRepository = workResolutionLogRepository;
    }

    public List<WorkComplaintResponse> getAssignedComplaints(String employeeEmail) {
        WorkUser employee = findEmployeeByEmail(employeeEmail);

        return workComplaintRepository.findAllByAssignedToIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WorkComplaintResponse updateComplaintStatus(Long complaintId,
            WorkUpdateComplaintStatusRequest request,
            String employeeEmail) {
        if (request == null || request.status() == null) {
            throw new IllegalArgumentException("status is required");
        }

        WorkUser employee = findEmployeeByEmail(employeeEmail);

        WorkComplaint complaint = workComplaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        if (complaint.getAssignedToId() == null || !complaint.getAssignedToId().equals(employee.getId())) {
            throw new IllegalArgumentException("You are not assigned to this complaint");
        }

        WorkComplaintStatus newStatus = request.status();

        if (newStatus == WorkComplaintStatus.CLOSED || newStatus == WorkComplaintStatus.RESOLVED) {
            if (request.resolutionComment() == null || request.resolutionComment().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Resolution comment is required when resolving or closing a complaint");
            }
        }

        complaint.setStatus(newStatus);
        complaint.setUpdatedAt(LocalDateTime.now());
        workComplaintRepository.save(complaint);

        if (request.resolutionComment() != null && !request.resolutionComment().trim().isEmpty()) {
            WorkResolutionLog log = new WorkResolutionLog();
            log.setComplaint(complaint);
            log.setUpdatedBy(employee);
            log.setComment(request.resolutionComment().trim());
            log.setStatus(newStatus);
            workResolutionLogRepository.save(log);
        }

        return toResponse(complaint);
    }

    private WorkUser findEmployeeByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required");
        }

        return workUserRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .filter(user -> user.getRole() == WorkUserRole.EMPLOYEE)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    private WorkComplaintResponse toResponse(WorkComplaint complaint) {
        return new WorkComplaintResponse(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getPriority(),
                complaint.getCreatedById(),
                complaint.getAssignedToId(),
                complaint.getAssignedById(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt());
    }
}
