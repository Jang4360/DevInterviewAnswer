package dev.interview.server.qna.dto;

import dev.interview.server.qna.domain.Qna;

import java.time.LocalDateTime;
import java.util.UUID;

// 오늘 복습할 질문 응답 DTO
public record QnaTodayResponse(
        UUID id,
        String question,
        String answer,
        LocalDateTime scheduleDate
) {
    public static QnaTodayResponse from(Qna qna) {
        return new QnaTodayResponse(
                qna.getId(),
                qna.getQuestion(),
                qna.getAnswer(),
                qna.getScheduledDate()
        );
    }
}
