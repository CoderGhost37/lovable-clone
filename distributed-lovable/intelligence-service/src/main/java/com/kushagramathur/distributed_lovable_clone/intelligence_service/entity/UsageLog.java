package com.kushagramathur.distributed_lovable_clone.intelligence_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usage_logs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"}) // One log per user per day
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate date;

    private Integer tokensUsed;
}