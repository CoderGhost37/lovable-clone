package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.chat.StreamResponse;
import com.kushagramathur.lovable_clone.entity.*;
import com.kushagramathur.lovable_clone.enums.ChatEventType;
import com.kushagramathur.lovable_clone.enums.MessageRole;
import com.kushagramathur.lovable_clone.error.ResourceNotFoundException;
import com.kushagramathur.lovable_clone.llm.LlmResponseParser;
import com.kushagramathur.lovable_clone.llm.PromptUtils;
import com.kushagramathur.lovable_clone.llm.advisors.FileTreeContextAdvisor;
import com.kushagramathur.lovable_clone.repository.*;
import com.kushagramathur.lovable_clone.security.AuthUtil;
import com.kushagramathur.lovable_clone.service.AiGenerationService;
import com.kushagramathur.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final LlmResponseParser llmResponseParser;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String message, Long projectId) {
        Long user = authUtil.getCurrentUserId();

        ChatSession chatSession = createChatSessionIfNotExists(user, projectId);

        Map<String, Object> advisorParams = Map.of(
                "userId", user,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(message)
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
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        long duration = (endTime.get() - startTime.get()) / 1000;
                        finalizeChats(message, chatSession, fullResponseBuffer.toString(), duration);
                    });
                })
                .doOnError(error -> log.error("Error during AI response streaming", error))
                .map(chatResponse -> {
                    String text = chatResponse.getResult().getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                });
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration) {
        Long projectId = chatSession.getProject().getId();

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .build()
        );

        ChatMessage assistantMessage = ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.ASSISTANT)
                        .build();
        assistantMessage = chatMessageRepository.save(assistantMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantMessage);
        chatEventList.add(0, ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantMessage)
                        .content("Thought for " + duration + "s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(event -> event.getType() == ChatEventType.FILE_EDIT)
                .forEach(event -> projectFileService.saveFile(projectId, event.getFilePath(), event.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            projectFileService.saveFile(projectId, filePath, fileContent);
        }
    }

    private ChatSession createChatSessionIfNotExists(Long userId, Long projectId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if (chatSession == null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();
            chatSessionRepository.save(chatSession);
        }

        return chatSession;
    }
}
