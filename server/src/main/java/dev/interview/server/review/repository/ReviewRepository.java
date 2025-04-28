package dev.interview.server.review.repository;

import dev.interview.server.review.domain.ReviewQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// 복습 기록 Repository
public interface ReviewRepository extends JpaRepository<ReviewQueue, UUID> {

    // 특정 QnA의 복습 기록 수
    Long countByQnaId(UUID qnaId);

    // 사용자의 전체 복습 기록 수
    Long countByUserId(UUID userId);
}
