package com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull ProjectRole role) {
}
