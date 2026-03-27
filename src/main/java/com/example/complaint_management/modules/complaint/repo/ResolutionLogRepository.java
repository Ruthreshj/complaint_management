package com.example.complaint_management.modules.complaint.repo;

import com.example.complaint_management.modules.complaint.domain.entity.ResolutionLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResolutionLogRepository extends JpaRepository<ResolutionLog, Long> {

    List<ResolutionLog> findAllByComplaintIdOrderByTimestampDesc(Long complaintId);
}