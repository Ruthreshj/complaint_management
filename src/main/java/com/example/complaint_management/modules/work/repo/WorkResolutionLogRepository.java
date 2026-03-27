package com.example.complaint_management.modules.work.repo;

import com.example.complaint_management.modules.work.domain.entity.WorkResolutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkResolutionLogRepository extends JpaRepository<WorkResolutionLog, Long> {
}
