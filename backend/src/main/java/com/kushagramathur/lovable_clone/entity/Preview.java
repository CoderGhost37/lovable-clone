package com.kushagramathur.lovable_clone.entity;

import com.kushagramathur.lovable_clone.enums.PreviewStatus;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Preview {
    private Long id;

    private Project project;

    private PreviewStatus status;

    private String namespace;

    private String podName;

    private String previewUrl;

    private Instant startedAt;

    private Instant terminatedAt;

    private Instant createdAt;
}
