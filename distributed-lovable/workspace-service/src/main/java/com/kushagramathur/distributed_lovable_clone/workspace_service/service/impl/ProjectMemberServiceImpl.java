package com.kushagramathur.distributed_lovable_clone.workspace_service.service.impl;

import com.kushagramathur.distributed_lovable_clone.common_lib.dto.UserDto;
import com.kushagramathur.distributed_lovable_clone.workspace_service.client.AccountClient;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member.InviteMemberRequest;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member.MemberResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member.UpdateRoleRequest;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.Project;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectMember;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectMemberId;
import com.kushagramathur.distributed_lovable_clone.common_lib.error.ResourceNotFoundException;
import com.kushagramathur.distributed_lovable_clone.workspace_service.mapper.ProjectMemberMapper;
import com.kushagramathur.distributed_lovable_clone.workspace_service.repository.ProjectMemberRepository;
import com.kushagramathur.distributed_lovable_clone.workspace_service.repository.ProjectRepository;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.kushagramathur.distributed_lovable_clone.workspace_service.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final AuthUtil authUtil;
    private final AccountClient accountClient;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {
        return projectMemberRepository
                    .findByIdProjectId(projectId)
                    .stream()
                    .map(projectMemberMapper::toProjectMemberResponseFromMember)
                    .toList();
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        UserDto invitee = accountClient.getUserByEmail(request.username()).orElseThrow(
                () -> new ResourceNotFoundException("User", userId.toString())
        );
        if (invitee.id().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.id());
        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Cannot invite once again");
        }

        ProjectMember member = ProjectMember
                .builder()
                .id(projectMemberId)
                .project(project)
                .role(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(member);

        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateRoleRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow(
                () -> new ResourceNotFoundException("ProjectMember", projectMemberId.toString())
        );

        projectMember.setRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Member not found in project");
        }

        projectMemberRepository.deleteById(projectMemberId);
    }

    // INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString())
        );
    }
}
