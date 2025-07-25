package dev.interview.server.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SummarizationServiceImpl implements SummarizationService{

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // 글 내용을 3문장으로 요약
    @Override
    public Mono<String> summarizeAsync(String content) {
        String url = "https://api.openai.com/v1/chat/completions";

        String prompt = "다음 글을 2~3 문장으로 요약해줘:\n" + content;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo-1106");
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", 100);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map responseBody = response;
                    List choices = (List) responseBody.get("choices");
                    Map choice = (Map) choices.get(0);
                    Map message = (Map) choice.get("message");
                    return (String) message.get("content");
                });
    }
}
