package com.kushagramathur.distributed_lovable_clone.workspace_service.entity;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.PreviewStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Preview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    private PreviewStatus status;

    private String namespace;

    private String podName;

    private String previewUrl;

    private Instant startedAt;

    private Instant terminatedAt;

    private Instant createdAt;
}
