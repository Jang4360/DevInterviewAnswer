package dev.interview.server.ai.gpt;

import dev.interview.server.ai.dto.GeneratedQnaResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GptClient {
    Mono<GeneratedQnaResponse> generateQuestionsAsync(String summary, List<String> similarSummaries);
}
