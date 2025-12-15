package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.member.InviteMemberRequest;
import com.kushagramathur.lovable_clone.dto.member.MemberResponse;
import com.kushagramathur.lovable_clone.dto.member.UpdateRoleRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
