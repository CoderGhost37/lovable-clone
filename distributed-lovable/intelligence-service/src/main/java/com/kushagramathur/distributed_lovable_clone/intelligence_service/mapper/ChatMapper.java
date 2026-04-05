package com.kushagramathur.distributed_lovable_clone.intelligence_service.mapper;

import com.kushagramathur.distributed_lovable_clone.intelligence_service.dto.chat.ChatResponse;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
