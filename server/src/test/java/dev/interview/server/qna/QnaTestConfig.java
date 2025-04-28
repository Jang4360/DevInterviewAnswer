package dev.interview.server.qna;

import dev.interview.server.qna.repository.QnaRepository;
import dev.interview.server.qna.service.QnaService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class QnaTestConfig {
    @Bean
    public QnaService qnaService() {
        return mock(QnaService.class);
    }

    @Bean
    public QnaRepository qnaRepository() {
        return mock(QnaRepository.class);
    }
}
