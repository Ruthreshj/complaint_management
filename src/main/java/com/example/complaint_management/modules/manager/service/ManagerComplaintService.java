package com.example.complaint_management.modules.manager.service;

import com.example.complaint_management.modules.manager.domain.entity.ManagerComplaint;
import com.example.complaint_management.modules.manager.domain.entity.ManagerUser;
import com.example.complaint_management.modules.manager.domain.enums.ManagerComplaintStatus;
import com.example.complaint_management.modules.manager.domain.enums.ManagerUserRole;
import com.example.complaint_management.modules.manager.dto.AssignComplaintRequest;
import com.example.complaint_management.modules.manager.dto.ManagerAssignResponse;
import com.example.complaint_management.modules.manager.dto.ManagerComplaintResponse;
import com.example.complaint_management.modules.manager.dto.ManagerEmployeeResponse;
import com.example.complaint_management.modules.manager.repo.ManagerComplaintRepository;
import com.example.complaint_management.modules.manager.repo.ManagerUserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ManagerComplaintService {

    private final ManagerComplaintRepository managerComplaintRepository;
    private final ManagerUserRepository managerUserRepository;

    public ManagerComplaintService(
            ManagerComplaintRepository managerComplaintRepository,
            ManagerUserRepository managerUserRepository
    ) {
        this.managerComplaintRepository = managerComplaintRepository;
        this.managerUserRepository = managerUserRepository;
    }

    public List<ManagerComplaintResponse> getAllComplaints() {
        return managerComplaintRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toComplaintResponse)
                .toList();
    }

    public List<ManagerEmployeeResponse> getEmployees() {
        return managerUserRepository.findAllByRoleOrderByNameAsc(ManagerUserRole.EMPLOYEE)
                .stream()
                .map(user -> new ManagerEmployeeResponse(user.getId(), user.getName(), user.getEmail()))
                .toList();
    }

    @Transactional
    public ManagerAssignResponse assignComplaint(Long complaintId, AssignComplaintRequest request, String managerEmail) {
        if (request == null || request.employeeId() == null) {
            throw new IllegalArgumentException("employeeId is required");
        }

        ManagerUser manager = managerUserRepository.findByEmailIgnoreCase(managerEmail)
                .filter(user -> user.getRole() == ManagerUserRole.MANAGER)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        ManagerUser employee = managerUserRepository.findById(request.employeeId())
                .filter(user -> user.getRole() == ManagerUserRole.EMPLOYEE)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        ManagerComplaint complaint = managerComplaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        complaint.setAssignedToId(employee.getId());
        complaint.setAssignedById(manager.getId());
        complaint.setStatus(ManagerComplaintStatus.ASSIGNED);
        complaint.setUpdatedAt(LocalDateTime.now());

        managerComplaintRepository.save(complaint);

        return new ManagerAssignResponse(
                complaint.getId(),
                employee.getId(),
                manager.getId(),
                complaint.getStatus(),
                "Complaint assigned successfully"
        );
    }

    private ManagerComplaintResponse toComplaintResponse(ManagerComplaint complaint) {
        return new ManagerComplaintResponse(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getPriority(),
                complaint.getCreatedById(),
                complaint.getAssignedToId(),
                complaint.getAssignedById(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt()
        );
    }
}
