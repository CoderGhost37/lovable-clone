package com.kushagramathur.distributed_lovable_clone.intelligence_service.entity;

import com.kushagramathur.distributed_lovable_clone.common_lib.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_events")
public class ChatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private ChatMessage chatMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatEventType type;

    @Column(nullable = false)
    private Integer sequenceOrder;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    private String filePath; // NULL unless FILE_EDIT

    @Column(columnDefinition = "text")
    private String metadata;

    @CreationTimestamp
    private Instant createdAt;

}
