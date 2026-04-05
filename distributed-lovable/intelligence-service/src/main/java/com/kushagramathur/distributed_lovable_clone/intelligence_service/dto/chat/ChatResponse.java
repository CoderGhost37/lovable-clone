package com.kushagramathur.distributed_lovable_clone.intelligence_service.dto.chat;

import com.kushagramathur.distributed_lovable_clone.intelligence_service.entity.ChatSession;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        ChatSession chatSession,
        MessageRole role,
        List<ChatEventResponse> events,
        String content,
        Integer tokensUsed,
        Instant createdAt
) {
}
