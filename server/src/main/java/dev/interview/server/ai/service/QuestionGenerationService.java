package dev.interview.server.ai.service;

import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import reactor.core.publisher.Mono;

public interface QuestionGenerationService {
    Mono<GeneratedQnaResponse> generateQuestionsAsync(GenerateQuestionRequest request);
}
