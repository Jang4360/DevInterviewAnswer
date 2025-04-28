package dev.interview.server.review;

import dev.interview.server.review.service.ReviewService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ReviewTestConfig {
    @Bean
    public ReviewService reviewService() {
        return mock(ReviewService.class);
    }
}
