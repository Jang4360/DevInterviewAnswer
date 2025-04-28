package dev.interview.server.ai.dto;

import java.util.UUID;

// 질문 생성 요청 시, 사용자 ID와 글 내용 전달
public record GenerateQuestionRequest(
        UUID userId,
        String content
){}
