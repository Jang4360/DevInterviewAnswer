package dev.interview.server.writing;

import dev.interview.server.writing.service.WritingService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class WritingTestConfig {
    @Bean
    public WritingService writingService() {
        return mock(WritingService.class);
    }
}
