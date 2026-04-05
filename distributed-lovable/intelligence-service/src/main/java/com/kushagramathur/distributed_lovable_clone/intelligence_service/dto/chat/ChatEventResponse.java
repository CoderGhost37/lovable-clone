package com.kushagramathur.distributed_lovable_clone.intelligence_service.dto.chat;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
