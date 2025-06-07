package dev.interview.server.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService{

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // 글 요약 -> 임베딩 벡터 생성 (OpenAI API 사용)
    @Override
    public Mono<List<Float>> createEmbeddingAsync(String text) {
        String url = "https://api.openai.com/v1/embeddings";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-embedding-3-small");
        requestBody.put("input", text);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map embeddingData = (Map) ((List) response.get("data")).get(0);
                    return (List<Float>) embeddingData.get("embedding");
                })
                .doOnError(e -> log.error("Embedding API 호출 실패: {}",e.getMessage()));
    }
}
