package dev.interview.server.ai.gpt;

import dev.interview.server.ai.dto.GeneratedQnaResponse;

import java.util.List;

public interface GptClient {
    GeneratedQnaResponse generateQuestions(String summary, List<String> similarSummaries);
}
