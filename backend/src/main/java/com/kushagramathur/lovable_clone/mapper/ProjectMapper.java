package com.kushagramathur.lovable_clone.mapper;

import com.kushagramathur.lovable_clone.dto.project.ProjectResponse;
import com.kushagramathur.lovable_clone.dto.project.ProjectSummaryResponse;
import com.kushagramathur.lovable_clone.entity.Project;
import com.kushagramathur.lovable_clone.enums.ProjectRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> project);
}
