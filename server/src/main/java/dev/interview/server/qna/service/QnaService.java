package dev.interview.server.qna.service;

import dev.interview.server.global.exception.ForbiddenException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.dto.QnaTodayResponse;
import dev.interview.server.qna.repository.QnaRepository;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.repository.WritingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// GPT 질문/답변 관리 비즈니스 로직
@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;
    private final WritingRepository writingRepository;

    // 질문 생성 저장
    @Transactional
    public Qna saveQna(UUID userId, UUID writingId, String question, String answer, LocalDateTime scheduledDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Writing writing = writingRepository.findById(writingId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 글입니다."));

        Qna qna = Qna.builder()
                .user(user)
                .writing(writing)
                .question(question)
                .answer(answer)
                .scheduledDate(scheduledDate)
                .isDeleted(false)
                .build();

        return qnaRepository.save(qna);
    }

    // 사용자별 질문 전체 조회
    public List<Qna> getAllByUserId(UUID userId) {
        return qnaRepository.findAllByUserId(userId);
    }

    // 사용자별 복습 대상 질문 조회
    @Transactional(readOnly = true)
    public List<QnaTodayResponse> getReviewQnasForToday(UUID userId) {
        List<Qna> qnaList = qnaRepository.findByUserIdAndScheduleDateAndReviewedFalse(userId, LocalDateTime.now());
        return qnaList.stream()
                .map(QnaTodayResponse::from)
                .toList();
    }

    // 사용자별 질문 삭제
    @Transactional
    public void deleteQna(UUID qnaId, UUID userId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 질문입니다."));
        if (!qna.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문만 삭제할 수 있습니다.");
        }
        qna.markAsDeleted(); // soft delete
    }

    //
    public Qna findById(String id) {
        return qnaRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("QnA를 찾을 수 없습니다"));
    }
}
