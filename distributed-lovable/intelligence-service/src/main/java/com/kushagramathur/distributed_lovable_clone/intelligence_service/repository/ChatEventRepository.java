package com.kushagramathur.distributed_lovable_clone.intelligence_service.repository;

import com.kushagramathur.distributed_lovable_clone.intelligence_service.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}
