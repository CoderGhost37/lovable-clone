package com.kushagramathur.lovable_clone.repository;

import com.kushagramathur.lovable_clone.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}
