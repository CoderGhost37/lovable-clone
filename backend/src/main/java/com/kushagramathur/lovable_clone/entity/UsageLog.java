package com.kushagramathur.lovable_clone.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class UsageLog {
    private Long id;

    private Project project;

    private User user;

    private String action;

    private Integer tokensUsed;

    private Integer durationMs;

    private String metaData;

    private Instant createdAt;
}
