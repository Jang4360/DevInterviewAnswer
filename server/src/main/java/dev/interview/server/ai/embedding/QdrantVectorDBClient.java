package dev.interview.server.ai.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Profile("!test")
@Component
@Slf4j
@RequiredArgsConstructor
public class QdrantVectorDBClient implements VectorDBClient {
    private final WebClient webClient;

    @Value("${qdrant.api.url}")
    private String qdrantApiUrl;

    private static final String COLLECTION_NAME = "user_vectors";

    @Override
    public Mono<Void> saveEmbeddingAsync(UUID userId, String summary, List<Float> embedding) {
        String url = qdrantApiUrl + "/collections/" + COLLECTION_NAME + "/points";

        Map<String, Object> point = Map.of(
                "id", UUID.randomUUID().toString(),
                "vector", embedding,
                "payload", Map.of(
                        "userId", userId.toString(),
                        "summary", summary
                )
        );

        Map<String, Object> requestBody = Map.of(
                "points", List.of(point)
        );

        return webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("✅ Qdrant 저장 완료"))
                .doOnError(e -> log.error("❌ Qdrant 저장 실패: {}", e.getMessage()));
    }

    @Override
    public List<String> searchSimilarSummaries(UUID userId, List<Float> embedding) {
        return searchSimilarSummariesAsync(userId, embedding).block(); // 비동기 호출을 동기로 래핑
    }

    @Override
    public Mono<List<String>> searchSimilarSummariesAsync(UUID userId, List<Float> embedding) {
        String url = qdrantApiUrl + "/collections/" + COLLECTION_NAME + "/points/search";

        Map<String, Object> requestBody = Map.of(
                "vector", embedding,
                "limit", 3,
                "filter", Map.of(
                        "must", List.of(
                                Map.of("key", "userId", "match", Map.of("value", userId.toString()))
                        )
                )
        );

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    List<Map<String, Object>> result = (List<Map<String, Object>>) response.get("result");
                    List<String> summaries = result.stream()
                            .map(item -> (Map<String, Object>) item.get("payload"))
                            .filter(Objects::nonNull)
                            .map(payload -> (String) payload.get("summary"))
                            .collect(Collectors.toList());
                    return Mono.just(summaries);
                });
    }
}
