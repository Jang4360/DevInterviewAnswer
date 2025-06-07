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

    // 전체 질문 생성 프로세스
    @Override
    public Mono<GeneratedQnaResponse> generateQuestionsAsync(GenerateQuestionRequest request) {
        String lockKey = "lock:generate:" + request.userId();
        boolean locked = redisLockService.tryLock(lockKey, Duration.ofMillis(500));
        if (!locked) {
            return Mono.error(new IllegalStateException("질문 생성 요청이 중복되었습니다."));
        }

        // 전체 프로세스 시작 시간 기록
        long overallStartTime = System.currentTimeMillis();

        return summarizationService.summarizeAsync(request.content())
                .doOnSuccess(s -> log.info("[성능 측정] 1. 요약 완료 ({}ms)", System.currentTimeMillis() - overallStartTime))
                .flatMap(summary -> {
                    long embeddingStartTime = System.currentTimeMillis();
                    return embeddingService.createEmbeddingAsync(summary)
                            .doOnSuccess(e -> log.info("[성능 측정] 2. 임베딩 생성 완료 ({}ms, 누적 {})",
                                    System.currentTimeMillis() - embeddingStartTime,
                                    System.currentTimeMillis() - overallStartTime))
                            .flatMap(embedding -> {
                                // Qdrant 저장 (백그라운드 처리) 시작 시간 기록
                                long qdrantSaveStartTime = System.currentTimeMillis();
                                vectorDBClient.saveEmbeddingAsync(request.userId(), summary, embedding)
                                        .doOnSuccess(v -> log.info("[성능 측정] 3. Qdrant 저장 완료 (백그라운드, {}ms)",
                                                System.currentTimeMillis() - qdrantSaveStartTime))
                                        .subscribe(
                                                v -> {}, // 성공 시 아무것도 하지 않음 (로그는 doOnSuccess에서 이미 처리)
                                                e -> log.error("백그라운드 Qdrant 저장 실패: {}", e.getMessage())
                                        );

                                // Qdrant 검색 시작 시간 기록 (이 시점부터 사용자 응답 시간에 영향)
                                long qdrantSearchStartTime = System.currentTimeMillis();
                                return vectorDBClient.searchSimilarSummariesAsync(request.userId(), embedding)
                                        .doOnSuccess(ss -> log.info("[성능 측정] 4. Qdrant 검색 완료 ({}ms, 누적 {})",
                                                System.currentTimeMillis() - qdrantSearchStartTime,
                                                System.currentTimeMillis() - overallStartTime))
                                        .flatMap(similarSummaries -> {
                                            long gptStartTime = System.currentTimeMillis();
                                            return gptClient.generateQuestionsAsync(summary, similarSummaries) // similarSummaries 전달
                                                    .doOnSuccess(resp -> log.info("[성능 측정] 5. GPT 질문 생성 완료 ({}ms, 누적 {})",
                                                            System.currentTimeMillis() - gptStartTime,
                                                            System.currentTimeMillis() - overallStartTime));
                                        });
                            });
                })
                .doFinally(signalType -> {
                    log.info("[성능 측정] 총 프로세스 완료: {}ms", System.currentTimeMillis() - overallStartTime);
                    redisLockService.unlock(lockKey);
                });
    }
}
