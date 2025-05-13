package dev.interview.server.qna.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.interview.server.user.domain.User;
import dev.interview.server.writing.domain.Writing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// GPT 가 생성한 면접 질문 및 답변 Entity
@Entity
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Qna {
    @Id
    @GeneratedValue
    @Column(name = "qna_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate; // 복습 시작일 (3,7,15...)

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Builder.Default
    @Column(nullable = false)
    private boolean reviewed = false;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writing_id")
    private Writing writing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateScheduledDate(LocalDateTime nextDate) {
        this.scheduledDate = nextDate;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
