package com.kushagramathur.lovable_clone.entity;

import com.kushagramathur.lovable_clone.enums.ProjectRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ProjectMemberId id;

    private Project project;

    private User user;

    private ProjectRole role;

    private Instant invitedAt;

    private Instant acceptedAt;
}
