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
        return generateQuestionsAsync(summary, similarSummaries).block(); // 비동기 호출을 동기로 래핑
    }

    // ✅ 비동기 메서드 추가
    @Override
    public Mono<GeneratedQnaResponse> generateQuestionsAsync(String summary, List<String> similarSummaries) {
        String url = "https://api.openai.com/v1/chat/completions";

        // 프롬프트 생성 (기존 코드 유지)
        String prompt = createPrompt(summary, similarSummaries);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo-1106");
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        // WebClient로 비동기 요청
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return Mono.fromCallable(() -> parseQnaItems(content))
                            .map(qnaItems -> new GeneratedQnaResponse(qnaItems));
                });
    }
    // QnA 아이템 파싱 메서드
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

    // 기존 프롬프트 생성 메서드 유지
    private String createPrompt(String summary, List<String> similarSummaries) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음 글 요약을 바탕으로 면접 질문 3개와 각 질문에 대한 모범 답변을 만들어줘. 질문 작성 시 다음 사항을 반영해야 해:\n" +
                "1. 단순 개념 확인이 아닌, 실무 상황과 연계된 질문 - 예를 들어 \"X가 무엇인가요?\" 대신 \"프로젝트에서 X를 사용할 때 발생할 수 있는 문제점과 해결 방안은?\"\n" +
                "2. 기술적 깊이를 확인할 수 있는 질문 - 표면적 이해가 아닌 원리와 내부 동작을 이해하는지 확인\n" +
                "3. 상황 기반 질문 포함 - \"만약 ~한 상황이라면 어떻게 대응할 것인지?\"\n" +
                "4. 적어도 하나는 기술 선택의 트레이드오프나 비교 관점을 물어보는 질문 - \"A 대신 B를 선택한 이유는?\"\n" +
                "\n" +
                "답변 작성 시 다음 사항을 반영해야 해:\n" +
                "1. 정확한 기술적 설명과 근거 제시 - 공식 문서나 검증된 기술 자료 기반\n" +
                "2. 실제 현업에서의 경험이 드러나는 구체적 사례나 상황 포함\n" +
                "3. 답변의 깊이와 넓이를 균형 있게 조절 - 너무 짧거나 불필요하게 길지 않게\n" +
                "4. 문제 해결 접근법과 사고 과정을 보여주는 구조화된 답변\n" +
                "5. 필요하다면 코드 예시나 다이어그램을 활용한 설명\n" +
                "6. 질문의 숨은 의도까지 파악하여 대응하는 전략적 답변\n+" +
                "글 요약을 분석하여 해당 기술 분야에서 가장 중요하고 실무에 필수적인 측면에 초점을 맞춘 질문들을 생성해줘.\n");
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

        return promptBuilder.toString();
    }
}
