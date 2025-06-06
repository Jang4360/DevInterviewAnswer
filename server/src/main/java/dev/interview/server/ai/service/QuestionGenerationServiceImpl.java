package dev.interview.server.ai.service;

import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import dev.interview.server.ai.embedding.VectorDBClient;
import dev.interview.server.ai.gpt.GptClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionGenerationServiceImpl implements QuestionGenerationService {
    private final GptClient gptClient;
    private final VectorDBClient vectorDBClient;
    private final SummarizationService summarizationService; // 글 요약 담당
    private final EmbeddingService embeddingService; // 임베딩 생성
    private final RedisLockService redisLockService;

    @Override
    public GeneratedQnaResponse generateQuestions(GenerateQuestionRequest request) {
        return generateQuestionsAsync(request)
                .doOnError(e -> log.error("질문 생성 실패: {}", e.getMessage()))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("질문 생성 실패"));
    }

    // 전체 질문 생성 프로세스
    @Override
    public Mono<GeneratedQnaResponse> generateQuestionsAsync(GenerateQuestionRequest request) {
        String lockKey = "lock:generate:" + request.userId();
        boolean locked = redisLockService.tryLock(lockKey, Duration.ofMillis(500));
        if (!locked) {
            return Mono.error(new IllegalStateException("질문 생성 요청이 중복되었습니다."));
        }

        // Option 1: 저장을 백그라운드에서 비동기 처리하고 검색은 즉시 수행 (결과에는 방금 저장한 임베딩 미포함)
        return summarizationService.summarizeAsync(request.content())
                .flatMap(summary -> embeddingService.createEmbeddingAsync(summary)
                        .flatMap(embedding -> {
                            // 저장은 별도의 Mono로 백그라운드에서 처리 (fire-and-forget 방식)
                            vectorDBClient.saveEmbeddingAsync(request.userId(), summary, embedding)
                                    .subscribe(
                                            v -> log.info("백그라운드 Qdrant 저장 완료"),
                                            e -> log.error("백그라운드 Qdrant 저장 실패: {}", e.getMessage())
                                    );

                            // 검색은 즉시 진행
                            return vectorDBClient.searchSimilarSummariesAsync(request.userId(), embedding)
                                    .flatMap(similarSummaries ->
                                            gptClient.generateQuestionsAsync(summary, similarSummaries)
                                    );
                        })
                )
                .doFinally(signalType -> redisLockService.unlock(lockKey));
    }
}
