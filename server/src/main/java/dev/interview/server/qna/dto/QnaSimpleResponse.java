package dev.interview.server.qna.dto;

import dev.interview.server.qna.domain.Qna;

import java.time.LocalDateTime;
import java.util.UUID;

// 전체 질문 응답 DTO
public record QnaSimpleResponse(
        UUID id,
        String question,
        LocalDateTime scheduleDate,
        boolean isDeleted
) {
    public static QnaSimpleResponse from(Qna qna) {
        return new QnaSimpleResponse(
                qna.getId(),
                qna.getQuestion(),
                qna.getScheduledDate(),
                qna.isDeleted()
        );
    }
}
