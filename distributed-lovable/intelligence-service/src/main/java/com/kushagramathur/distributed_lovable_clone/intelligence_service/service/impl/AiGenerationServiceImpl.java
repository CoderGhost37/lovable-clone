package com.kushagramathur.distributed_lovable_clone.intelligence_service.service.impl;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ChatEventStatus;
import com.kushagramathur.distributed_lovable_clone.common_lib.event.FileStoreRequestEvent;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.client.WorkspaceClient;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.dto.chat.StreamResponse;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.entity.*;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ChatEventType;
import com.kushagramathur.distributed_lovable_clone.common_lib.enums.MessageRole;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.llm.LlmResponseParser;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.llm.PromptUtils;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.llm.advisors.FileTreeContextAdvisor;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.llm.tools.CodeGenerationTools;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.repository.*;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.service.AiGenerationService;
import com.kushagramathur.distributed_lovable_clone.intelligence_service.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final LlmResponseParser llmResponseParser;
    private final UsageService usageService;
    private final WorkspaceClient workspaceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String message, Long projectId) {
        usageService.checkDailyTokensUsage();

        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = createChatSessionIfNotExists(userId, projectId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectId, workspaceClient);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(message)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String content = response.getResult().getOutput().getText();
                    if (content != null && !content.isEmpty()) {
                        if (endTime.get() == 0L) {
                            endTime.set(System.currentTimeMillis());
                        }
                        fullResponseBuffer.append(content);
                    }

                    if (response.getMetadata().getUsage() != null) {
                        usageRef.set(response.getMetadata().getUsage());
                    }
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        long duration = (endTime.get() - startTime.get()) / 1000;
                        finalizeChats(message, chatSession, fullResponseBuffer.toString(), duration, usageRef.get(), userId);
                    });
                })
                .doOnError(error -> log.error("Error during AI response streaming", error))
                .map(chatResponse -> {
                    String text = chatResponse.getResult().getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                });
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage, Long userId) {
        Long projectId = chatSession.getId().getProjectId();

        if (usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(totalTokens);
        }

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantMessage = ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.ASSISTANT)
                        .tokensUsed(usage.getCompletionTokens())
                        .build();
        assistantMessage = chatMessageRepository.save(assistantMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantMessage);
        chatEventList.add(0, ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .status(ChatEventStatus.CONFIRMED)
                        .chatMessage(assistantMessage)
                        .content("Thought for " + duration + "s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(event -> event.getType() == ChatEventType.FILE_EDIT)
                .forEach(event -> {
                    String sagaId = UUID.randomUUID().toString();
                    FileStoreRequestEvent fileStoreRequestEvent = new FileStoreRequestEvent(
                            projectId,
                            sagaId,
                            event.getFilePath(),
                            event.getContent(),
                            userId
                    );
                    kafkaTemplate.send("file-storage-request-event", "project"+projectId, fileStoreRequestEvent);
                });

        chatEventRepository.saveAll(chatEventList);
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            // TODO: trigger kafka event for file edits here, so workspace service can listen and update files in real-time
        }
    }

    private ChatSession createChatSessionIfNotExists(Long userId, Long projectId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if (chatSession == null) {
            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .build();
            chatSessionRepository.save(chatSession);
        }

        return chatSession;
    }
}
