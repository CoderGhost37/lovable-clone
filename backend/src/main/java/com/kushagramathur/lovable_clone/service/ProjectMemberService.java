package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.member.InviteMemberRequest;
import com.kushagramathur.lovable_clone.dto.member.MemberResponse;
import com.kushagramathur.lovable_clone.dto.member.UpdateRoleRequest;
import org.jspecify.annotations.Nullable;

public interface ProjectMemberService {
    MemberResponse getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateRoleRequest request, Long userId);

    MemberResponse deleteProjectMember(Long projectId, Long memberId, Long userId);
}
