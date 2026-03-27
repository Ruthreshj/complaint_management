package com.example.complaint_management.modules.manager.repo;

import com.example.complaint_management.modules.manager.domain.entity.ManagerComplaint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerComplaintRepository extends JpaRepository<ManagerComplaint, Long> {

    List<ManagerComplaint> findAllByOrderByCreatedAtDesc();
}
