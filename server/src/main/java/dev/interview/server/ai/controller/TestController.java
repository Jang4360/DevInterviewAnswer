package dev.interview.server.ai.controller;

import dev.interview.server.ai.dto.ErrorResponse;
import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import dev.interview.server.ai.service.QuestionGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final QuestionGenerationService questionGenerationService;

    // 질문 생성 API - 실제 API 호출
    @PostMapping("/generate-questions")
    public ResponseEntity<?> generateQuestions(@RequestBody Map<String, String> request) {
        try {
            // 요청 데이터 추출
            String content = request.get("content");
            String userId = request.get("userId");

            // UUID 변환
            UUID uuid;
            try {
                uuid = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("잘못된 UUID 형식입니다.");
            }

            // 요청 객체 생성
            GenerateQuestionRequest generateQuestionRequest = new GenerateQuestionRequest(uuid, content);

            // 질문 생성 호출
            GeneratedQnaResponse response = questionGenerationService.generateQuestions(generateQuestionRequest);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse("중복 요청입니다. 잠시 후 다시 시도해 주세요"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
        }
    }
}

