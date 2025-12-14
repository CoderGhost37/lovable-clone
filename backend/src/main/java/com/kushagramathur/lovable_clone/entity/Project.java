package com.kushagramathur.lovable_clone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "projects",
        indexes = {
                @Index(name = "idx_projects_updated_at_desc", columnList = "updated_at DESC, deleted_at"),
                @Index(name = "idx_projects_deleted_at_updated_at_desc", columnList = "deleted_at, updated_at DESC"),
                @Index(name = "idx_project_deleted_at", columnList = "deleted_at")
        }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Boolean isPublic = false;

    private Instant deletedAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
