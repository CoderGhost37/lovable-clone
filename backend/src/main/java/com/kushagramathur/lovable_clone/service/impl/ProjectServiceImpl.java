package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.project.ProjectRequest;
import com.kushagramathur.lovable_clone.dto.project.ProjectResponse;
import com.kushagramathur.lovable_clone.dto.project.ProjectSummaryResponse;
import com.kushagramathur.lovable_clone.entity.Project;
import com.kushagramathur.lovable_clone.entity.ProjectMember;
import com.kushagramathur.lovable_clone.entity.ProjectMemberId;
import com.kushagramathur.lovable_clone.entity.User;
import com.kushagramathur.lovable_clone.enums.ProjectRole;
import com.kushagramathur.lovable_clone.error.ResourceNotFoundException;
import com.kushagramathur.lovable_clone.mapper.ProjectMapper;
import com.kushagramathur.lovable_clone.repository.ProjectMemberRepository;
import com.kushagramathur.lovable_clone.repository.ProjectRepository;
import com.kushagramathur.lovable_clone.repository.UserRepository;
import com.kushagramathur.lovable_clone.security.AuthUtil;
import com.kushagramathur.lovable_clone.service.ProjectService;
import com.kushagramathur.lovable_clone.service.ProjectTemplateService;
import com.kushagramathur.lovable_clone.service.SubscriptionService;
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
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;
    private final ProjectTemplateService projectTemplateService;

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
        if (!subscriptionService.canCreateNewProject()) {
            throw new RuntimeException("Project creation limit reached for your subscription plan.");
        }

        Long userId = authUtil.getCurrentUserId();

        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }

        // Get a reference for the user (no db call for full entity) - will be used to set membership.
        User owner = userRepository.getReferenceById(userId);

        Project project = Project
                .builder()
                .name(request.name())
                .isPublic(false)
                .build();

        // Persist project first so we have an id to reference in ProjectMember
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .role(ProjectRole.OWNER)
                .user(owner)
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

    // INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
