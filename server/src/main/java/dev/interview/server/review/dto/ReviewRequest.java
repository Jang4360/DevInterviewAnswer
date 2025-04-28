package dev.interview.server.review.dto;

import java.util.UUID;

// 복습 이력 저장 요청 DTO
public record ReviewRequest(
        UUID userId,
        UUID qnaId
) {}
