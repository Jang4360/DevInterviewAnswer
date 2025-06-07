package dev.interview.server.ai.service;

import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import reactor.core.publisher.Mono;

public interface QuestionGenerationService {
    GeneratedQnaResponse generateQuestions(GenerateQuestionRequest request);
    Mono<GeneratedQnaResponse> generateQuestionsAsync(GenerateQuestionRequest request);
}
