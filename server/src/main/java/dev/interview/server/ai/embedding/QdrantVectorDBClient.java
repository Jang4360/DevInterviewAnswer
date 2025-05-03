package dev.interview.server.ai.embedding;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;

@Profile("!test")
@Component
@Slf4j
@RequiredArgsConstructor
public class QdrantVectorDBClient implements VectorDBClient {
    private final WebClient webClient;

    @Value("${qdrant.api.url}")
    private String qdrantApiUrl;

    @Value("${qdrant.api.key}")
    private String qdrantApiKey;

    private static final String COLLECTION_NAME = "user_vectors";

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", qdrantApiKey);  // ✅ KEY 설정
        return headers;
    }

    // Qdrant 컬렉션 초기화 (없으면 생성)
    @PostConstruct
    public void initCollection() {
        String url = qdrantApiUrl + "/collections/" + COLLECTION_NAME;

        Map<String, Object> vectors = Map.of(
                "size", 1536,
                "distance", "Cosine"
        );

        Map<String, Object> requestBody = Map.of(
                "vectors", vectors
        );

        try {
            webClient.put()
                    .uri(url)
                    .headers(headers -> headers.addAll(getAuthHeaders()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("✅ Qdrant 컬렉션 생성 완료");
        } catch (Exception e) {
            log.warn("⚠️ Qdrant 컬렉션 생성 실패 또는 이미 존재: {}", e.getMessage());
        }
    }

    @Override
    public void saveEmbedding(UUID userId, String summary, List<Float> embedding) {
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

        Map response = webClient.put()
                .uri(url)
                .headers(headers -> headers.addAll(getAuthHeaders()))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("✅ Qdrant 저장 응답: {}", response);
    }

    @Override
    public List<String> searchSimilarSummaries(UUID userId, List<Float> embedding) {
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

        Map response = webClient.post()
                .uri(url)
                .headers(headers -> headers.addAll(getAuthHeaders()))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> result = (List<Map<String, Object>>) response.get("result");

        List<String> summaries = new ArrayList<>();
        for (Map<String, Object> item : result) {
            Map<String, Object> payload = (Map<String, Object>) item.get("payload");
            if (payload != null && payload.get("summary") != null) {
                summaries.add((String) payload.get("summary"));
            } else {
                log.warn("⚠️ payload 누락: {}", item);
            }
        }

        return summaries;
    }
}
