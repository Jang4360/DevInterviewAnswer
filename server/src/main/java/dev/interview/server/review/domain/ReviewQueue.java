package dev.interview.server.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// 복습 완료 기록 Entity
@Entity
@Table(name = "review_queue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReviewQueue {
    @Id
    @GeneratedValue
    @Column(name = "review_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id")
    private Qna qna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
