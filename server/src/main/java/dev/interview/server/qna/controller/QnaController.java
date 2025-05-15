package dev.interview.server.qna.controller;

import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.dto.QnaCreateRequest;
import dev.interview.server.qna.dto.QnaCreateResponse;
import dev.interview.server.qna.dto.QnaSimpleResponse;
import dev.interview.server.qna.dto.QnaTodayResponse;
import dev.interview.server.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/qna")
@Tag(name = "QnA", description = "GPT 질문 생성/복습 관련 API")
public class QnaController {
    private final QnaService qnaService;

    // GPT 가 생성한 질문/답변 저장하는 API
    @PostMapping("/{writingId}")
    @Operation(summary = "GPT 질문 저장", description = "GPT가 생성한 질문을 받아 DB에 저장합니다.")
    public ResponseEntity<QnaCreateResponse> createQna(
            @PathVariable UUID writingId,
            @RequestBody QnaCreateRequest request
            ) {
        var saved = qnaService.saveQna(
                request.userId(),
                writingId,
                request.question(),
                request.answer(),
                LocalDateTime.now()
        );
        return ResponseEntity.ok(new QnaCreateResponse(saved.getId()));
    }

    // 사용자별 오늘 복습할 질문 조회하는 API
    @GetMapping("/today")
    @Operation(summary = "오늘 복습할 질문 조회", description = "오늘 날짜 기준 복습 스케줄이 지난 질문들을 조회합니다.")
    public ResponseEntity<List<QnaTodayResponse>> getTodayReviewQnas(@RequestParam UUID userId) {
        List<QnaTodayResponse> result = qnaService.getReviewQnasForToday(userId);
        return ResponseEntity.ok(result);
    }

    // 사용자별 전체 질문 조회하는 API
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 질문 전체 조회", description = "특정 사용자가 생성한 모든 질문을 조회합니다.")
    public ResponseEntity<Page<QnaSimpleResponse>> getQnaListByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable //  Pageable 파라미터 추가
    ) {
        Page<QnaSimpleResponse> response = qnaService.getQnaListWithReviewCount(userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 사용자별 질문 삭제하는 API
    @DeleteMapping("/{qnaId}")
    @Operation(summary = "질문 삭제", description = "질문을 삭제 처리합니다.")
    public ResponseEntity<Void> deleteQna(@PathVariable UUID qnaId, @RequestParam UUID userId) {
        qnaService.deleteQna(qnaId,userId);
        return ResponseEntity.noContent().build();
    }

    // (QnA id로) 하나의 질문 데이터를 조회하는 API
    @GetMapping("/{id}")
    public ResponseEntity<Qna> getQnaById(@PathVariable String id) {
        Qna qna = qnaService.findById(id);
        if (qna != null) {
            return ResponseEntity.ok(qna);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 누적 질문 수 조회하는 API
    @GetMapping("user/{userId}/count")
    @Operation(summary = "사용자 질문 수 조회", description = "특정 사용자가 생성한 질문 개수를 조회합니다")
    public ResponseEntity<Long> getQuestionCountByUSeer(@PathVariable UUID userId) {
        Long count = qnaService.countByUser(userId);
        return ResponseEntity.ok(count);
    }
}
