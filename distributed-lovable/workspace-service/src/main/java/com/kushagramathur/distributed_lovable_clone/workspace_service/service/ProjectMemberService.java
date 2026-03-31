package com.kushagramathur.distributed_lovable_clone.workspace_service.service;

import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member.InviteMemberRequest;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member.MemberResponse;
import com.kushagramathur.distributed_lovable_clone.workspace_service.dto.member.UpdateRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
