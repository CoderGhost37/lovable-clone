package com.kushagramathur.lovable_clone.dto.chat;

import com.kushagramathur.lovable_clone.entity.ChatSession;
import com.kushagramathur.lovable_clone.enums.MessageRole;

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
