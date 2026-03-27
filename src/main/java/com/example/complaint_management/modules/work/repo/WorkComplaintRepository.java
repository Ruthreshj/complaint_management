package com.example.complaint_management.modules.work.repo;

import com.example.complaint_management.modules.work.domain.entity.WorkComplaint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkComplaintRepository extends JpaRepository<WorkComplaint, Long> {

    List<WorkComplaint> findAllByAssignedToIdOrderByCreatedAtDesc(Long assignedToId);
}
