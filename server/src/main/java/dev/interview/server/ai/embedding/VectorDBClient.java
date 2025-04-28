package dev.interview.server.ai.embedding;

import java.util.List;
import java.util.UUID;

public interface VectorDBClient {
    void saveEmbedding(UUID userId, String summary, List<Float> embedding);
    List<String> searchSimilarSummaries(UUID userId, List<Float> embedding);
}
