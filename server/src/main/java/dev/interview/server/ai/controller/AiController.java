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
    public Mono<ResponseEntity<?>> generateQuestions(@RequestBody GenerateQuestionRequest request) {
        return questionGenerationService.generateQuestionsAsync(request) // 비동기 메서드 직접 반환
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalStateException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                .body(new ErrorResponse("중복 요청입니다. 잠시 후 다시 시도해 주세요"))));
    }
}
