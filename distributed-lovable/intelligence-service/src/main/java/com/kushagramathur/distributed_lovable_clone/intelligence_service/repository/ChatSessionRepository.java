package com.kushagramathur.distributed_lovable_clone.intelligence_service.repository;

import com.kushagramathur.distributed_lovable_clone.intelligence_service.entity.ChatSession;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
