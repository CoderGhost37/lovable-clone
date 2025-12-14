package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.project.ProjectRequest;
import com.kushagramathur.lovable_clone.dto.project.ProjectResponse;
import com.kushagramathur.lovable_clone.dto.project.ProjectSummaryResponse;
import com.kushagramathur.lovable_clone.entity.Project;
import com.kushagramathur.lovable_clone.entity.User;
import com.kushagramathur.lovable_clone.error.ResourceNotFoundException;
import com.kushagramathur.lovable_clone.mapper.ProjectMapper;
import com.kushagramathur.lovable_clone.repository.ProjectRepository;
import com.kushagramathur.lovable_clone.repository.UserRepository;
import com.kushagramathur.lovable_clone.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        var projects = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListOfProjectSummaryResponse(projects);
    }

    @Override
    public ProjectResponse getUserProjectById(Long id, Long userId) {
        Project project = getAccessibleProjectById(id, userId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow();
        Project project = Project
                .builder()
                .name(request.name())
                .isPublic(false)
                .owner(owner)
                .build();
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        Project project = getAccessibleProjectById(id, userId);
        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {
        Project project = getAccessibleProjectById(id, userId);
        if (project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete the project");
        }

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    // INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
