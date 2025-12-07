package com.kushagramathur.lovable_clone.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String stripePriceId;

    private Integer maxProjects;

    private Integer maxTokensPerDay;

    private Integer maxPreviews;

    private Boolean unlimitedAi;

    private Boolean active;
}
