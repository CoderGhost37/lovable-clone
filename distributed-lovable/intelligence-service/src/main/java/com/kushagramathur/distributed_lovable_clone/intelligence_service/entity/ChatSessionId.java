package com.kushagramathur.distributed_lovable_clone.intelligence_service.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ChatSessionId implements Serializable {
    private Long projectId;
    private Long userId;
}
