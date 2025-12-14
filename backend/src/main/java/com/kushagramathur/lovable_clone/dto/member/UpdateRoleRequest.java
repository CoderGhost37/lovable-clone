package com.kushagramathur.lovable_clone.dto.member;

import com.kushagramathur.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull ProjectRole role) {
}
