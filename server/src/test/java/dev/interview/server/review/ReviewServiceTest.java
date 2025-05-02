package dev.interview.server.review;

import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.repository.QnaRepository;
import dev.interview.server.review.domain.ReviewQueue;
import dev.interview.server.review.repository.ReviewRepository;
import dev.interview.server.review.service.ReviewService;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QnaRepository qnaRepository;

    // 리뷰 저장 성공 테스트
    @Test
    void recordAndReschedule_success_firstReview() {
        // given
        UUID userId = UUID.randomUUID();
        UUID qnaId = UUID.randomUUID();

        User user = User.builder().id(userId).build();
        Qna qna = Qna.builder().id(qnaId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(qna));
        when(reviewRepository.countByQnaId(qnaId)).thenReturn(1L);

        // when
        reviewService.recordAndReschedule(userId, qnaId);

        // then
        verify(reviewRepository).save(any(ReviewQueue.class));
        verify(qnaRepository).findById(qnaId);
        verify(reviewRepository).countByQnaId(qnaId);
        assertNotNull(qna.getScheduledDate());
    }

    // 유저 못찾음 스케줄 저장 실패
    @Test
    void recordAndReschedule_fail_userNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID qnaId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> reviewService.recordAndReschedule(userId, qnaId));
    }

    @Test
    void recordAndReschedule_fail_qnaNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID qnaId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(qnaRepository.findById(qnaId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> reviewService.recordAndReschedule(userId, qnaId));
    }

    @Test
    void getReviewCounterByQna_success() {
        // given
        UUID qnaId = UUID.randomUUID();
        when(reviewRepository.countByQnaId(qnaId)).thenReturn(5L);

        // when
        Long count = reviewService.getReviewCounterByQna(qnaId);

        // then
        assertEquals(5L, count);
    }

    @Test
    void getReviewCounterByUser_success() {
        // given
        UUID userId = UUID.randomUUID();
        when(reviewRepository.countByUserId(userId)).thenReturn(10L);

        // when
        long count = reviewService.getReviewCounterByUser(userId);

        // then
        assertEquals(10L, count);
    }

    @Test
    void getLatestReviewDate_success() {
        //given
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ReviewQueue latestReview = ReviewQueue.builder()
                .user(User.builder().id(userId).build())
                .reviewedAt(now)
                .build();

        when(reviewRepository.findTopByUserIdOrderByReviewedAtDesc(userId))
                .thenReturn(Optional.of(latestReview));

        // when
        LocalDateTime result = reviewService.getLatestReviewDate(userId);

        // then
        assertEquals(now,result);
        verify(reviewRepository).findTopByUserIdOrderByReviewedAtDesc(userId);
    }

    @Test
    void getLatestReviewDate_notFound() {
        // given
        UUID userId = UUID.randomUUID();
        when(reviewRepository.findTopByUserIdOrderByReviewedAtDesc(userId))
                .thenReturn(Optional.empty());

        // when
        LocalDateTime result = reviewService.getLatestReviewDate(userId);

        // then
        assertNull(result);
        verify(reviewRepository).findTopByUserIdOrderByReviewedAtDesc(userId);
    }
}
