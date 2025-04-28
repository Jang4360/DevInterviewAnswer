package dev.interview.server.writing.dto;

import dev.interview.server.writing.domain.Writing;

import java.util.UUID;

// 글 작성 응답 DTO
public record WritingCreateResponse(
        UUID id,
        String content,
        UUID userId
) {
    public static WritingCreateResponse from(Writing writing) {
        return new WritingCreateResponse(
                writing.getId(),
                writing.getContent(),
                writing.getUser().getId()
        );
    }
}
