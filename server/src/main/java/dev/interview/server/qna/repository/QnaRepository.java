package dev.interview.server.qna.repository;

import dev.interview.server.qna.domain.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// GPT 가 생성한 질문/답변 Repository
public interface QnaRepository extends JpaRepository<Qna,UUID> {

    // 사용자별 질문 목록 조회 (복습 리스트 표시)
    List<Qna> findAllByUserId(UUID userId);

    // 특정 날짜 기준 복습 대상 질문 조회
    List<Qna> findAllByUserIdAndScheduledDateBeforeAndIsDeletedFalse(UUID userId, LocalDateTime today);
}
