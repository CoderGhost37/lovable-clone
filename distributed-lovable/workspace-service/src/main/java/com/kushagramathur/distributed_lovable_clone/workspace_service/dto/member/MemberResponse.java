package com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole role,
        Instant invitedAt
) {
}
