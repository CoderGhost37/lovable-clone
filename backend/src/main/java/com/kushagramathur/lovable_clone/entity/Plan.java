package com.kushagramathur.lovable_clone.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Plan {
    private Long id;

    private String name;

    private String stripePriceId;

    private Integer maxProjects;

    private Integer maxTokensPerDay;

    private Integer maxPreviews;

    private Boolean unlimitedAi;

    private Boolean active;
}
