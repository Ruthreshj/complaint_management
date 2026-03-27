package com.example.complaint_management.modules.complaint.repo;

import com.example.complaint_management.modules.complaint.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
