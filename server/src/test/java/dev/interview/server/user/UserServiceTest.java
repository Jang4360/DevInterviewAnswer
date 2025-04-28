package dev.interview.server.user;

import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void saveUser_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("1234")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        User savedUser = userService.save(user);

        // then
        assertNotNull(savedUser);
        assertEquals(userId, savedUser.getId());
        verify(userRepository).save(user);
    }
}
