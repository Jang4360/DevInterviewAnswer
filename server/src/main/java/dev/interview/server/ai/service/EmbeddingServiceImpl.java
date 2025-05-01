package dev.interview.server.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService{

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // 글 요약 -> 임베딩 벡터 생성 (OpenAI API 사용)
    @Override
    public List<Float> createEmbedding(String text) {
        String url = "https://api.openai.com/v1/embeddings";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-embedding-3-small");
        requestBody.put("input", text);

        Map response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, clientResponse -> {
                    return Mono.error(new RuntimeException("429 Too Many Requests"));
                })
                .bodyToMono(Map.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("429")))
                .block();

        Map embeddingData = (Map) ((List) response.get("data")).get(0);
        System.out.println("생성된 Embedding: " + embeddingData);
        return (List<Float>) embeddingData.get("embedding");
    }
}
