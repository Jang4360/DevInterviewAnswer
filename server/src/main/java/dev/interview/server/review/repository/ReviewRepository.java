package dev.interview.server.review.repository;

import dev.interview.server.review.domain.ReviewQueue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 복습 기록 Repository
public interface ReviewRepository extends JpaRepository<ReviewQueue, UUID> {

    // 특정 QnA의 복습 기록 수
    Long countByQnaId(UUID qnaId);

    // 사용자의 전체 복습 기록 수
    Long countByUserId(UUID userId);

    // 누적 질문 수
    Optional<ReviewQueue> findTopByUserIdOrderByReviewedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"user", "qna"})
    List<ReviewQueue> findAllByUserId(UUID userId);

}
