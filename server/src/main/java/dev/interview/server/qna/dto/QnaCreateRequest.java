package dev.interview.server.qna.dto;

import java.util.List;
import java.util.UUID;

// GPT 질문 생성 요청 DTO
public record QnaCreateRequest(
        UUID userId,
        String question,
        String answer
) { }
