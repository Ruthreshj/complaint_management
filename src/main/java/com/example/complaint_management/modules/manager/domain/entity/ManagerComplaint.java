package com.example.complaint_management.modules.manager.domain.entity;

import com.example.complaint_management.modules.manager.domain.enums.ManagerComplaintPriority;
import com.example.complaint_management.modules.manager.domain.enums.ManagerComplaintStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "complaints")
public class ManagerComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManagerComplaintStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManagerComplaintPriority priority;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @Column(name = "assigned_by_id")
    private Long assignedById;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
