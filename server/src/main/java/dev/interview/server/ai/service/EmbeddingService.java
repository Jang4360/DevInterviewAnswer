package dev.interview.server.ai.service;

import reactor.core.publisher.Mono;

import java.util.List;

public interface EmbeddingService {
    List<Float> createEmbedding(String text);
    Mono<List<Float>> createEmbeddingAsync(String text);
}
