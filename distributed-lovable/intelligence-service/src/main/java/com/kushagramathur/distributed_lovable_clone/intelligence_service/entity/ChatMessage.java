package com.kushagramathur.distributed_lovable_clone.intelligence_service.entity;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id" , referencedColumnName = "projectId", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    })
    private ChatSession chatSession;

    @Column(columnDefinition = "text")
    private String content;  // NULL unless role is USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    List<ChatEvent> events;  // Empty list if role is USER

    private Integer tokensUsed = 0;

    @CreationTimestamp
    private Instant createdAt;
}
