package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);
}
