package dev.interview.server.qna.dto;

import dev.interview.server.qna.domain.Qna;

import java.time.LocalDateTime;
import java.util.UUID;

// 전체 질문 응답 DTO
public record QnaSimpleResponse(
        UUID id,
        String question,
        LocalDateTime scheduleDate,
        boolean isDeleted,
        int reviewCount
) {
    // 복습 횟수를 함께 받아서 응답으로 생성하는 메서드
    public static QnaSimpleResponse from(Qna qna, Long reviewCount) {
        return new QnaSimpleResponse(
                qna.getId(),
                qna.getQuestion(),
                qna.getScheduledDate(),
                qna.isDeleted(),
                reviewCount != null ? reviewCount.intValue() : 0  // Long 타입을 int로 변환
        );
    }

    // 복습 횟수가 없는 경우
    public static QnaSimpleResponse from(Qna qna) {
        return new QnaSimpleResponse(
                qna.getId(),
                qna.getQuestion(),
                qna.getScheduledDate(),
                qna.isDeleted(),
                0  // 기본값 0으로 설정
        );
    }
}