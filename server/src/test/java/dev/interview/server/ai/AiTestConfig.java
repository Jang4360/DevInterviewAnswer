package dev.interview.server.ai;

import dev.interview.server.ai.embedding.VectorDBClient;
import dev.interview.server.ai.gpt.GptClient;
import dev.interview.server.ai.service.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiTestConfig {

    @Bean
    public GptClient gptClient() {
        return Mockito.mock(GptClient.class);
    }

    @Bean
    public VectorDBClient vectorDBClient() {
        return Mockito.mock(VectorDBClient.class);
    }

    @Bean
    public EmbeddingService embeddingService() {
        return Mockito.mock(EmbeddingService.class);
    }

    @Bean
    public SummarizationService summarizationService() {
        return Mockito.mock(SummarizationService.class);
    }

    @Bean
    public QuestionGenerationService questionGenerationService(
            GptClient gptClient,
            VectorDBClient vectorDBClient,
            SummarizationService summarizationService,
            EmbeddingService embeddingService
    ) {
        return new QuestionGenerationServiceImpl(gptClient, vectorDBClient, summarizationService, embeddingService);
    }
}
