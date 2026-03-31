package com.kushagramathur.distributed_lovable_clone.workspace_service.repository;

import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectMember;
import com.kushagramathur.distributed_lovable_clone.workspace_service.entity.ProjectMemberId;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ProjectRole;
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
    int countProjectsOwnedByUser(@Param("userId") Long userId);
}
