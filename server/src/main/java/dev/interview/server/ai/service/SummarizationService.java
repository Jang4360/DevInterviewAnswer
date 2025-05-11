package dev.interview.server.ai.service;

import reactor.core.publisher.Mono;

public interface SummarizationService {
    String summarize(String content);
    Mono<String> summarizeAsync(String content);
}
