package com.kushagramathur.distributed_lovable_clone.workspace_service.service.impl;

import com.kushagramathur.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.kushagramathur.distributed_lovable_clone.common_lib.dto.UserDto;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectPermission;
import com.kushagramathur.distributed_lovable_clone.workspace_service.client.AccountClient;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.ProjectRequest;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.ProjectResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.project.ProjectSummaryResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.Project;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectMember;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectMemberId;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectRole;
import com.kushagramathur.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import com.kushagramathur.distributed_lovable_clone.workspace_service.mapper.ProjectMapper;
import com.kushagramathur.distributed_lovable_clone.workspace_service.repository.ProjectMemberRepository;
import com.kushagramathur.distributed_lovable_clone.workspace_service.repository.ProjectRepository;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.kushagramathur.distributed_lovable_clone.workspace_service.security.SecurityExpressions;
import com.kushagramathur.distributed_lovable_clone.workspace_service.service.ProjectService;
import com.kushagramathur.distributed_lovable_clone.workspace_service.service.ProjectTemplateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;
    private final ProjectTemplateService projectTemplateService;
    private final AccountClient accountClient;
    private final SecurityExpressions securityExpressions;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projectsWithRole = projectRepository.findAllAccessibleByUser(userId);
        return projectsWithRole.stream()
                .map(pr -> projectMapper.toProjectSummaryResponse(pr.getProject(), pr.getRole()))
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#id)")
    public ProjectSummaryResponse getUserProjectById(Long id) {
        Long userId = authUtil.getCurrentUserId();
        var projectWithRole = projectRepository
                .findAccessibleProjectByIdWithRole(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole());
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        if (!canUserCreateProject()) {
            throw new RuntimeException("Project creation limit reached for your subscription plan.");
        }

        Long ownerUserId = authUtil.getCurrentUserId();

        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }

        Project project = Project
                .builder()
                .name(request.name())
                .isPublic(false)
                .build();

        // Persist project first so we have an id to reference in ProjectMember
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), ownerUserId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .role(ProjectRole.OWNER)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);

        projectTemplateService.initializeProjectFromTemplate(project.getId());

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    @Override
    public boolean hasPermission(Long projectId, ProjectPermission permission) {
        return securityExpressions.hasPermission(projectId, permission);
    }

    // INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }

    private boolean canUserCreateProject() {
        Long userId = authUtil.getCurrentUserId();
        if (userId == null) {
            return false;
        }

        PlanDto currentPlan = accountClient.getCurrentSubscribedPlan();

        int maxAllowedProjects = currentPlan.maxProjects();
        int ownedProjectsCount = projectMemberRepository.countProjectsOwnedByUser(userId);

        return ownedProjectsCount < maxAllowedProjects;
    }
}
