package dev.interview.server.ai.controller;

import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import dev.interview.server.ai.service.QuestionGenerationServiceImpl;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<GeneratedQnaResponse> generateQuestions(@RequestBody GenerateQuestionRequest request) {
        GeneratedQnaResponse response = questionGenerationService.generateQuestions(request);
        return ResponseEntity.ok(response);
    }
}
