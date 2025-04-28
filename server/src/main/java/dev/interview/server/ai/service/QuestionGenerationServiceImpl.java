package dev.interview.server.ai.service;

import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import dev.interview.server.ai.embedding.VectorDBClient;
import dev.interview.server.ai.gpt.GptClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionGenerationServiceImpl implements QuestionGenerationService {
    private final GptClient gptClient;
    private final VectorDBClient vectorDBClient;
    private final SummarizationService summarizationService; // 글 요약 담당
    private final EmbeddingService embeddingService; // 임베딩 생성

    // 전체 질문 생성 프로세스
    @Override
    public GeneratedQnaResponse generateQuestions(GenerateQuestionRequest request) {
        log.info("generateQuestions 호출됨 - userId: {}, content: {}", request.userId(), request.content());
        // 1. 글 요약 생성
        String summary = summarizationService.summarize(request.content());

        // 2. 요약 → 임베딩 생성
        List<Float> embedding = embeddingService.createEmbedding(summary);

        // 3. Vector DB 저장
        vectorDBClient.saveEmbedding(request.userId(), summary, embedding);

        // 4. 유사 글 검색
        List<String> similarSummaries = vectorDBClient.searchSimilarSummaries(request.userId(), embedding);

        // 5. GPT 질문 생성
        GeneratedQnaResponse response = gptClient.generateQuestions(summary, similarSummaries);

        return response;
    }
}
