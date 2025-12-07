package com.kushagramathur.lovable_clone.entity;

import com.kushagramathur.lovable_clone.enums.ProjectRole;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ProjectMember {
    private ProjectMemberId id;

    private Project project;

    private User user;

    private ProjectRole role;

    private Instant invitedAt;

    private Instant acceptedAt;
}
