package com.kushagramathur.lovable_clone.entity;

import com.kushagramathur.lovable_clone.enums.MessageRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ChatSession chatSession;

    private String content;

    private MessageRole role;

    private String toolCalls;

    private Integer tokensUsed;

    private Instant createdAt;
}
