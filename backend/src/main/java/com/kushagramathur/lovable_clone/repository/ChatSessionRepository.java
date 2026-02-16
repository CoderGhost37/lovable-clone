package com.kushagramathur.lovable_clone.repository;

import com.kushagramathur.lovable_clone.entity.ChatSession;
import com.kushagramathur.lovable_clone.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
