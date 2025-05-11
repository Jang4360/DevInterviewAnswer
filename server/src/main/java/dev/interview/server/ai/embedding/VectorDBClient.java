package dev.interview.server.ai.embedding;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface VectorDBClient {
    void saveEmbedding(UUID userId, String summary, List<Float> embedding);
    List<String> searchSimilarSummaries(UUID userId, List<Float> embedding);

    Mono<Void> saveEmbeddingAsync(UUID userId, String summary, List<Float> embedding);
    Mono<List<String>> searchSimilarSummariesAsync(UUID userId, List<Float> embedding);
}
