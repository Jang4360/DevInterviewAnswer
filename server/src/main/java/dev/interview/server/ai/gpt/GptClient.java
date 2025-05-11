package dev.interview.server.ai.gpt;

import dev.interview.server.ai.dto.GeneratedQnaResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GptClient {
    GeneratedQnaResponse generateQuestions(String summary, List<String> similarSummaries);

    Mono<GeneratedQnaResponse> generateQuestionsAsync(String summary, List<String> similarSummaries);
}
