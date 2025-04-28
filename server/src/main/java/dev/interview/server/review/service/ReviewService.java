package dev.interview.server.review.service;

import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.repository.QnaRepository;
import dev.interview.server.review.domain.ReviewQueue;
import dev.interview.server.review.repository.ReviewRepository;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// 복습 이력 관련 비즈니스 로직
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;

    // 복습 완료 + 다음 스케줄 계산
    @Transactional
    public void recordAndReschedule(UUID userId, UUID qnaId) {
        // 1. 사용자 QnA 조회 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 질문입니다."));

        // 2. 복습 이력 저장
        ReviewQueue review = ReviewQueue.builder()
                .user(user)
                .qna(qna)
                .reviewedAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        // 3. 복습 횟수 확인
        long count = reviewRepository.countByQnaId(qnaId);

        // 4. 다음 스케줄 계산
        LocalDateTime nextSchedule = null;
        if (count == 1) nextSchedule = LocalDateTime.now().plusDays(1);
        else if (count == 2) nextSchedule = LocalDateTime.now().plusDays(7);
        else if (count == 3) nextSchedule = LocalDateTime.now().plusDays(15);
        else if (count == 4) nextSchedule = LocalDateTime.now().plusDays(30);

        // 5. Qna 에 다음 복습 일자 갱신
        qna.updateScheduledDate(nextSchedule);
    }

    // 특정 qna 복습 횟수 조회
    public Long getReviewCounterByQna(UUID qnaId) {
        return reviewRepository.countByQnaId(qnaId);
    }

    // 사용자 전체 복습 횟수 조회
    public long getReviewCounterByUser(UUID userId) {
        return reviewRepository.countByUserId(userId);
    }
}
