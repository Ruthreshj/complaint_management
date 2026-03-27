package com.example.complaint_management.modules.work.repo;

import com.example.complaint_management.modules.work.domain.entity.WorkUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkUserRepository extends JpaRepository<WorkUser, Long> {

    Optional<WorkUser> findByEmailIgnoreCase(String email);
}
