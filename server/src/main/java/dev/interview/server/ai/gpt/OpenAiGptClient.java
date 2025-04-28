package dev.interview.server.ai.gpt;

import dev.interview.server.ai.dto.GeneratedQnaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiGptClient implements GptClient{

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // 요약 및 유사글을 기반으로 GPT 에게 질문 / 답변 요청
    @Override
    public GeneratedQnaResponse generateQuestions(String summary, List<String> similarSummaries) {
        String url = "https://api.openai.com/v1/chat/completions";

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음 글 요약을 바탕으로 면접 질문 3개와 각 질문에 대한 모범 답변을 만들어줘.\n\n");
        promptBuilder.append("현재 글 요약:\n").append(summary).append("\n\n");

        if (!similarSummaries.isEmpty()) {
            promptBuilder.append("유사 글 요약들:\n");
            for (String s : similarSummaries) {
                promptBuilder.append("- ").append(s).append("\n");
            }
            promptBuilder.append("\n");
        }

        promptBuilder.append("형식:\n");
        promptBuilder.append("1. 질문: ...\n   답변: ...\n");
        promptBuilder.append("2. 질문: ...\n   답변: ...\n");
        promptBuilder.append("3. 질문: ...\n   답변: ...\n");

        String prompt = promptBuilder.toString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo-1106");
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        Map<String, Object> response = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, clientResponse -> {
                    return Mono.error(new RuntimeException("429 Too Many Requests"));
                })
                .bodyToMono(Map.class)
                .delaySubscription(Duration.ofSeconds(1))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("429")))
                .block();

        Map choice = (Map) ((List) response.get("choices")).get(0);
        Map message = (Map) choice.get("message");
        String content = (String) message.get("content");

        return new GeneratedQnaResponse(parseQnaItems(content));
    }

    // GPT 응답을 질문/답변 형태로 파싱 -> 질문/답변 리스트 추출
    private List<GeneratedQnaResponse.QnaItem> parseQnaItems(String content) {
        List<GeneratedQnaResponse.QnaItem> qnaItems = new ArrayList<>();
        String[] lines = content.split("\n");
        String currentQuestion = null;
        String currentAnswer = null;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.")) {
                if (currentQuestion != null && currentAnswer != null) {
                    qnaItems.add(new GeneratedQnaResponse.QnaItem(currentQuestion, currentAnswer));
                }
                currentQuestion = line.replaceFirst("^[1-3]\\. 질문[:：]?", "").trim();
                currentAnswer = null;
            } else if (line.startsWith("답변") || line.startsWith("답:")) {
                currentAnswer = line.replaceFirst("^답변[:：]?", "").trim();
            } else {
                if (currentAnswer != null) {
                    currentAnswer += " " + line;
                }
            }
        }

        if (currentQuestion != null && currentAnswer != null) {
            qnaItems.add(new GeneratedQnaResponse.QnaItem(currentQuestion, currentAnswer));
        }

        return qnaItems;
    }
}
