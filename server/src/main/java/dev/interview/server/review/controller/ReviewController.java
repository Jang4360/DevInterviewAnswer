package dev.interview.server.review.controller;

import dev.interview.server.review.dto.ReviewCountResponse;
import dev.interview.server.review.dto.ReviewRequest;
import dev.interview.server.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Tag(name = "Review", description = "복습 이력 관련 API")
public class ReviewController {
    private final ReviewService reviewService;

    // 복습 이력 저장, 다음 스케줄 업데이트 API
    @PostMapping
    @Operation(summary = "복습 완료 처리", description = "복습 로그를 저장하고 QnA의 다음 스케줄을 계산합니다.")
    public ResponseEntity<Void> review(@RequestBody ReviewRequest request) {
        reviewService.recordAndReschedule(request.userId(), request.qnaId());
        return ResponseEntity.ok().build();
    }

    // 특정 질문 복습 횟수 조회 API
    @GetMapping("/qna/{qnaId}/count")
    @Operation(summary = "질문 복습 횟수 조회", description = "특정 질문의 누적 복습 횟수 조회합니다.")
    public ResponseEntity<ReviewCountResponse> getReviewCountByQna(@PathVariable UUID qnaId) {
        Long count = reviewService.getReviewCounterByQna(qnaId);
        return ResponseEntity.ok(new ReviewCountResponse(count));
    }

    // 사용자의 누적 복습 횟수 조회 API
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "사용자 복습 횟수 조회", description = "특정 사용자의 누적 복습 횟수를 조회합니다.")
    public ResponseEntity<ReviewCountResponse> getReviewCountByUser(@PathVariable UUID userId) {
        Long count = reviewService.getReviewCounterByUser(userId);
        return ResponseEntity.ok(new ReviewCountResponse(count));
    }

    // 최근 복습일 조회 API
    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "최근 복습일 조회", description = "특정 사용자의 가장 최근 복습일을 조회합니다")
    public ResponseEntity<LocalDateTime> getLatestReviewDate(@PathVariable UUID userId) {
        LocalDateTime latest = reviewService.getLatestReviewDate(userId);
        return ResponseEntity.ok(latest);
    }
}
