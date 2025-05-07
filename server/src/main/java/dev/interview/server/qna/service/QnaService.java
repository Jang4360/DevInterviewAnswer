package dev.interview.server.qna.service;

import dev.interview.server.global.exception.ForbiddenException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.dto.QnaTodayResponse;
import dev.interview.server.qna.repository.QnaRepository;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.user.service.UserService;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.repository.WritingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// GPT 질문/답변 관리 비즈니스 로직
@Service
@RequiredArgsConstructor
@Slf4j
public class QnaService {
    private final QnaRepository qnaRepository;
    private final UserService userService;
    private final WritingRepository writingRepository;

    // 질문 생성 저장
    @Transactional
    public Qna saveQna(UUID userId, UUID writingId, String question, String answer, LocalDateTime scheduledDate) {
        log.info("질문 생성 시도: userId={}, question={}", userId, question);
        User user = userService.getUserByIdOrThrow(userId);
        Writing writing = writingRepository.findById(writingId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 글입니다."));

        Qna qna = Qna.builder()
                .user(user)
                .writing(writing)
                .question(question)
                .answer(answer)
                .scheduledDate(scheduledDate)
                .isDeleted(false)
                .reviewed(false)
                .build();

        Qna savedQna = qnaRepository.save(qna);

        log.info("질문 생성 성공: qnaId={}", savedQna.getId());
        return savedQna;
    }

    // 사용자별 질문 전체 조회
    @Transactional(readOnly = true)
    public Page<Qna> getAllByUserId(UUID userId, Pageable pageable) {
        return qnaRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable);
    }

    // 사용자별 복습 대상 질문 조회
    @Transactional(readOnly = true)
    public List<QnaTodayResponse> getReviewQnasForToday(UUID userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Qna> qnaList = qnaRepository
                .findByUserIdAndScheduledDateBetweenAndReviewedFalseAndIsDeletedFalse(userId, start, end);
        return qnaList.stream().map(QnaTodayResponse::from).toList();
    }

    // 사용자별 질문 삭제
    @Transactional
    public void deleteQna(UUID qnaId, UUID userId) {
        log.info("질문 삭제 시도: qnaId={}, userId={}", qnaId, userId);
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> {
                    log.warn("질문 삭제 실패 - 존재하지 않음: qnaId={}", qnaId);
                    return new NotFoundException("존재하지 않는 질문입니다.");
                });
        if (!qna.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문만 삭제할 수 있습니다.");
        }
        qna.markAsDeleted(); // soft delete
        log.info("질문 삭제 성공: qnaId={}", qnaId);
    }

    // 질문 하나 조회
    @Transactional(readOnly = true)
    public Qna findById(String id) {
        return qnaRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("QnA를 찾을 수 없습니다"));
    }

    // 전체 질문수 조회
    @Transactional(readOnly = true)
    public Long countByUser(UUID userId) {
        return qnaRepository.countByUserIdAndIsDeletedFalse(userId);
    }
}
