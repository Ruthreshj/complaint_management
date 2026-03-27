package com.example.complaint_management.modules.complaint.repo;

import com.example.complaint_management.modules.complaint.domain.entity.Complaint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findAllByCreatedByIdOrderByCreatedAtDesc(Long createdById);

    List<Complaint> findAllByOrderByCreatedAtDesc();
}
