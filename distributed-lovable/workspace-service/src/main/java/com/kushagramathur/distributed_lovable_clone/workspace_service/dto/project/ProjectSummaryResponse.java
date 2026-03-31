package com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(
        Long id,
        String name,
        ProjectRole role,
        Instant createdAt,
        Instant updatedAt
) {
}
