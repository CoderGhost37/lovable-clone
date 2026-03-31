package com.kushagramathur.distributed_lovable_clone.workspace_service.mapper;

import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.ProjectResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.ProjectSummaryResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.Project;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> project);
}
