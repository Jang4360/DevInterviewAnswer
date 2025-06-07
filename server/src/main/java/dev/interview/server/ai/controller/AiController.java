package dev.interview.server.ai.controller;

import dev.interview.server.ai.dto.ErrorResponse;
import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import dev.interview.server.ai.service.QuestionGenerationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {
    private final QuestionGenerationServiceImpl questionGenerationService;

// 질문 생성 API
    @PostMapping("/generate-questions")
    public ResponseEntity<?> generateQuestions(@RequestBody GenerateQuestionRequest request) {
        try {

            GeneratedQnaResponse response = questionGenerationService.generateQuestions(request);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)

                    .body(new ErrorResponse("중복 요청입니다. 잠시 후 다시 시도해 주세요"));

        }
    }
}
