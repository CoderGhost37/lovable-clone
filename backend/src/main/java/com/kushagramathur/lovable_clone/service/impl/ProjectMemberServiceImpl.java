package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.member.InviteMemberRequest;
import com.kushagramathur.lovable_clone.dto.member.MemberResponse;
import com.kushagramathur.lovable_clone.dto.member.UpdateRoleRequest;
import com.kushagramathur.lovable_clone.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {
    @Override
    public MemberResponse getProjectMembers(Long projectId, Long userId) {
        return null;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId) {
        return null;
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateRoleRequest request, Long userId) {
        return null;
    }

    @Override
    public MemberResponse deleteProjectMember(Long projectId, Long memberId, Long userId) {
        return null;
    }
}
