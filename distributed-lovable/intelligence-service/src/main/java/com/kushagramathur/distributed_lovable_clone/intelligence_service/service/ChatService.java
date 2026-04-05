package com.kushagramathur.distributed_lovable_clone.intelligence_service.service;

import com.kushagramathur.distributed_lovable_clone.intelligence_service.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);
}
