package dev.interview.server.ai.dto;

import java.util.List;

// GPT 로부터 생성된 질문/답변 리스트를 담는 응답 DTO
public record GeneratedQnaResponse(
        List<QnaItem> qnaList
) {
    public record QnaItem(
            String question,
            String answer
){}}
