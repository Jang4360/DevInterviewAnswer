package dev.interview.server.writing.dto;

import java.util.UUID;

// 글 생성 요청 DTO
public record WritingCreateRequest(
        UUID userId,
        String content
) {}
