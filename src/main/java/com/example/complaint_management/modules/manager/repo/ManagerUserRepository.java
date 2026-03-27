package com.example.complaint_management.modules.manager.repo;

import com.example.complaint_management.modules.manager.domain.entity.ManagerUser;
import com.example.complaint_management.modules.manager.domain.enums.ManagerUserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerUserRepository extends JpaRepository<ManagerUser, Long> {

    Optional<ManagerUser> findByEmailIgnoreCase(String email);

    List<ManagerUser> findAllByRoleOrderByNameAsc(ManagerUserRole role);
}
