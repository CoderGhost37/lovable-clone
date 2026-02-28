package com.kushagramathur.lovable_clone.repository;

import com.kushagramathur.lovable_clone.entity.ProjectMember;
import com.kushagramathur.lovable_clone.entity.ProjectMemberId;
import com.kushagramathur.lovable_clone.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findByIdProjectId(Long projectId);

    @Query("""
            SELECT pm.role FROM ProjectMember pm
            WHERE pm.id.projectId = :projectId
            AND pm.id.userId = :userId
            """)
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId,
                                                       @Param("userId") Long userId);

    @Query("""
            SELECT COUNT(pm) FROM ProjectMember pm
            WHERE pm.role = 'OWNER'
            AND pm.id.userId = :userId
            """)
    int countProjectOwnedByUser(@Param("userId") Long userId);
}
