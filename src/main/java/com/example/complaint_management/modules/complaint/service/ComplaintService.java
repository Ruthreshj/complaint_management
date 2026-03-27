package com.example.complaint_management.modules.complaint.service;

import com.example.complaint_management.modules.complaint.domain.entity.Complaint;
import com.example.complaint_management.modules.complaint.domain.entity.User;
import com.example.complaint_management.modules.complaint.domain.enums.ComplaintStatus;
import com.example.complaint_management.modules.complaint.dto.ComplaintResponse;
import com.example.complaint_management.modules.complaint.dto.CreateComplaintRequest;
import com.example.complaint_management.modules.complaint.repo.ComplaintRepository;
import com.example.complaint_management.modules.complaint.repo.ComplaintUserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintUserRepository complaintUserRepository;

    public ComplaintService(ComplaintRepository complaintRepository, ComplaintUserRepository complaintUserRepository) {
        this.complaintRepository = complaintRepository;
        this.complaintUserRepository = complaintUserRepository;
    }

    public ComplaintResponse createComplaint(String requesterEmail, CreateComplaintRequest request) {
        validateCreateRequest(request);

        User createdBy = complaintUserRepository.findByEmail(requesterEmail.trim().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("Authenticated user not found"));

        Complaint complaint = new Complaint();
        complaint.setTitle(request.title().trim());
        complaint.setDescription(request.description().trim());
        complaint.setPriority(request.priority());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setCreatedBy(createdBy);

        Complaint saved = complaintRepository.save(complaint);
        return toResponse(saved);
    }

    public ComplaintResponse getComplaintById(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with id: " + complaintId));
        return toResponse(complaint);
    }

    public List<ComplaintResponse> getMyComplaints(String requesterEmail) {
        User requester = complaintUserRepository.findByEmail(requesterEmail.trim().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("Authenticated user not found"));

        return complaintRepository.findAllByCreatedByIdOrderByCreatedAtDesc(requester.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateCreateRequest(CreateComplaintRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (isBlank(request.title())) {
            throw new IllegalArgumentException("Title is required");
        }
        if (isBlank(request.description())) {
            throw new IllegalArgumentException("Description is required");
        }
        if (request.priority() == null) {
            throw new IllegalArgumentException("Priority is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ComplaintResponse toResponse(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getPriority(),
                complaint.getCreatedBy() != null ? complaint.getCreatedBy().getId() : null,
                complaint.getAssignedTo() != null ? complaint.getAssignedTo().getId() : null,
                complaint.getAssignedBy() != null ? complaint.getAssignedBy().getId() : null,
                complaint.getCreatedAt(),
                complaint.getUpdatedAt());
    }
}
