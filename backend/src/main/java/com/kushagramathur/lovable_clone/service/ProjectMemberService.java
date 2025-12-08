package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.member.InviteMemberRequest;
import com.kushagramathur.lovable_clone.dto.member.MemberResponse;
import com.kushagramathur.lovable_clone.dto.member.UpdateRoleRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateRoleRequest request, Long userId);

    void removeProjectMember(Long projectId, Long memberId, Long userId);
}
