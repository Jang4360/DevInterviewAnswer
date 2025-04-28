package dev.interview.server.ai.service;

import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;

public interface QuestionGenerationService {
    GeneratedQnaResponse generateQuestions(GenerateQuestionRequest request);
}
