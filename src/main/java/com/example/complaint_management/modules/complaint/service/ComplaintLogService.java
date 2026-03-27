package com.example.complaint_management.modules.complaint.service;

import com.example.complaint_management.modules.complaint.domain.entity.Complaint;
import com.example.complaint_management.modules.complaint.domain.entity.ResolutionLog;
import com.example.complaint_management.modules.complaint.domain.entity.User;
import com.example.complaint_management.modules.complaint.domain.enums.UserRole;
import com.example.complaint_management.modules.complaint.dto.CreateResolutionLogRequest;
import com.example.complaint_management.modules.complaint.dto.ResolutionLogResponse;
import com.example.complaint_management.modules.complaint.repo.ComplaintRepository;
import com.example.complaint_management.modules.complaint.repo.ComplaintUserRepository;
import com.example.complaint_management.modules.complaint.repo.ResolutionLogRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class ComplaintLogService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintUserRepository complaintUserRepository;
    private final ResolutionLogRepository resolutionLogRepository;

    public ComplaintLogService(
            ComplaintRepository complaintRepository,
            ComplaintUserRepository complaintUserRepository,
            ResolutionLogRepository resolutionLogRepository
    ) {
        this.complaintRepository = complaintRepository;
        this.complaintUserRepository = complaintUserRepository;
        this.resolutionLogRepository = resolutionLogRepository;
    }

    @Transactional
    public ResolutionLogResponse createEmployeeLog(Long complaintId, String requesterEmail, CreateResolutionLogRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.comment() == null || request.comment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is required");
        }
        if (request.status() == null) {
            throw new IllegalArgumentException("Status is required");
        }

        User employee = complaintUserRepository.findByEmail(requesterEmail.trim().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("Authenticated user not found"));

        if (employee.getRole() != UserRole.EMPLOYEE) {
            throw new IllegalStateException("Only employees can add complaint logs");
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with id: " + complaintId));

        if (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(employee.getId())) {
            throw new IllegalStateException("Only the assigned employee can add logs for this complaint");
        }

        ResolutionLog log = new ResolutionLog();
        log.setComplaint(complaint);
        log.setUpdatedBy(employee);
        log.setComment(request.comment().trim());
        log.setStatus(request.status());

        complaint.setStatus(request.status());
        complaintRepository.save(complaint);

        ResolutionLog saved = resolutionLogRepository.save(log);
        return toResponse(saved);
    }

    public List<ResolutionLogResponse> getLogsByComplaintId(Long complaintId) {
        complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with id: " + complaintId));

        return resolutionLogRepository.findAllByComplaintIdOrderByTimestampDesc(complaintId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResolutionLogResponse toResponse(ResolutionLog log) {
        return new ResolutionLogResponse(
                log.getId(),
                log.getComplaint().getId(),
                log.getUpdatedBy().getId(),
                log.getComment(),
                log.getStatus(),
                log.getTimestamp()
        );
    }
}