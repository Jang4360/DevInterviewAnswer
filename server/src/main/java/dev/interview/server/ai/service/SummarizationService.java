package dev.interview.server.ai.service;

import reactor.core.publisher.Mono;

public interface SummarizationService {
    Mono<String> summarizeAsync(String content);
}
