package dev.interview.server.review.dto;

// 복습 이력 횟수 응답 DTO
public record ReviewCountResponse(long count) {
    public static ReviewCountResponse from(Long count) {
        return new ReviewCountResponse(count);
    }
}
