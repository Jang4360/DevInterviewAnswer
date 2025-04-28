package dev.interview.server.user;

import dev.interview.server.user.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class UserTestConfig {
    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }
}
