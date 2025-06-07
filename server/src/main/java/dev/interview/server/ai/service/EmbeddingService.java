package dev.interview.server.ai.service;

import reactor.core.publisher.Mono;

import java.util.List;

public interface EmbeddingService {
    Mono<List<Float>> createEmbeddingAsync(String text);
}
