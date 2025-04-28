package dev.interview.server.qna.dto;

import java.util.UUID;

// GPT 질문 생성 응답 DTO
public record QnaCreateResponse(
        UUID qnaId
) {}
