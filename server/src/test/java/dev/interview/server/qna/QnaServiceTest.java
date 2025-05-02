package dev.interview.server.qna;

import dev.interview.server.global.exception.ForbiddenException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.dto.QnaTodayResponse;
import dev.interview.server.qna.repository.QnaRepository;
import dev.interview.server.qna.service.QnaService;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.repository.WritingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QnaServiceTest {
    @InjectMocks
    private QnaService qnaService;

    @Mock
    private QnaRepository qnaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WritingRepository writingRepository;

    // 저장 성공일 때
    @Test
    void saveQna_success() {
        //given
        UUID userId = UUID.randomUUID();
        UUID writingId = UUID.randomUUID();
        String question = "What is Spring?";
        String answer = "Spring is a Java framework";

        User user = User.builder()
                .id(userId)
                .build();

        Writing writing = Writing.builder()
                .id(writingId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(writingRepository.findById(writingId)).thenReturn(Optional.of(writing));
        when(qnaRepository.save(any(Qna.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Qna result = qnaService.saveQna(userId, writingId, question, answer, LocalDateTime.now());

        // then
        assertNotNull(result);
        assertEquals(question, result.getQuestion());
        verify(qnaRepository).save(any(Qna.class));
    }

    // 존재하지 않는 User 일 때 예외
    @Test
    void saveQna_userNotFound() {
        UUID userId = UUID.randomUUID();
        UUID writingId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            qnaService.saveQna(userId, writingId, "Q", "A", LocalDateTime.now());
        });
    }

    // 존재하지 않는 Writing 일 때 예외
    @Test
    void saveQna_writingNotFound() {
        UUID userId = UUID.randomUUID();
        UUID writingId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(writingRepository.findById(writingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            qnaService.saveQna(userId, writingId, "Q", "A", LocalDateTime.now());
        });
    }

    // 유저 조회
    @Test
    void getAllByUserId_success() {
        UUID userId = UUID.randomUUID();
        Qna qna = Qna.builder().id(UUID.randomUUID()).build();

        when(qnaRepository.findAllByUserIdAndIsDeletedFalse(userId)).thenReturn(List.of(qna));

        List<Qna> result = qnaService.getAllByUserId(userId);

        assertEquals(1, result.size());
        verify(qnaRepository).findAllByUserIdAndIsDeletedFalse(userId);
    }

    // 오늘 리뷰 qna 조회
    @Test
    void getReviewQnasForToday_success() {
        UUID userId = UUID.randomUUID();
        Qna qna = Qna.builder()
                .id(UUID.randomUUID())
                .scheduledDate(LocalDateTime.now())
                .isDeleted(false)
                .reviewed(false)
                .build();
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        when(qnaRepository.findByUserIdAndScheduledDateBetweenAndReviewedFalseAndIsDeletedFalse(eq(userId), eq(start), eq(end)))
                .thenReturn(List.of(qna));

        List<QnaTodayResponse> result = qnaService.getReviewQnasForToday(userId);

        assertEquals(1, result.size());
        verify(qnaRepository).findByUserIdAndScheduledDateBetweenAndReviewedFalseAndIsDeletedFalse(eq(userId), eq(start), eq(end));
    }

    // 삭제 성공 테스트
    @Test
    void deleteQna_success() {
        UUID qnaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Qna qna = Qna.builder()
                .id(qnaId)
                .user(User.builder().id(userId).build())
                .isDeleted(false)
                .build();

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(qna));

        qnaService.deleteQna(qnaId,userId);

        assertEquals(true,qna.isDeleted());
    }

    // 존재하지 않는 Qna
    @Test
    void deleteQna_qnaNotFound() {
        UUID qnaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            qnaService.deleteQna(qnaId, userId);
        });
    }

    // 권한 없음
    @Test
    void deleteQna_forbidden() {
        UUID qnaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Qna qna = Qna.builder()
                .id(qnaId)
                .user(User.builder().id(otherUserId).build())
                .build();

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(qna));

        assertThrows(ForbiddenException.class, () -> {
            qnaService.deleteQna(qnaId, userId);
        });
    }

    // QnA 단일 조회 성공
    @Test
    void findById_success() {
        UUID qnaId = UUID.randomUUID();
        Qna qna = Qna.builder().id(qnaId).question("Test?").build();

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(qna));

        Qna result = qnaService.findById(qnaId.toString());

        assertEquals("Test?", result.getQuestion());
        verify(qnaRepository).findById(qnaId);
    }

    // QnA 단일 조회 실패
    @Test
    void findById_fail() {
        UUID qnaId = UUID.randomUUID();

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> qnaService.findById(qnaId.toString()));
    }

    // 질문 수 카운트 성공
    @Test
    void countByUser_success() {
        UUID userId = UUID.randomUUID();

        when(qnaRepository.countByUserIdAndIsDeletedFalse(userId)).thenReturn(5L);

        Long count = qnaService.countByUser(userId);

        assertEquals(5L,count);
        verify(qnaRepository).countByUserIdAndIsDeletedFalse(userId);
    }
}
