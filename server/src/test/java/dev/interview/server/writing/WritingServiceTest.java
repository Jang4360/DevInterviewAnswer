package dev.interview.server.writing;

import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.repository.WritingRepository;
import dev.interview.server.writing.service.WritingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WritingServiceTest {
    @InjectMocks
    private WritingService writingService;

    @Mock
    private WritingRepository writingRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void getWritingsByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        Writing writing = Writing.builder()
                .id(UUID.randomUUID())
                .content("Spring Study")
                .user(User.builder().id(userId).build())
                .build();

        when(writingRepository.findAllByUserId(userId)).thenReturn(List.of(writing));

        // when
        List<Writing> writings = writingService.getWritingsByUserId(userId);

        // then
        assertEquals(1, writings.size());
        assertEquals("Spring Study", writings.get(0).getContent());
        verify(writingRepository).findAllByUserId(userId);
    }

    @Test
    void createWriting_success() {
        // given
        UUID userId = UUID.randomUUID();
        String content = "Spring Core Concepts";

        User user = User.builder().id(userId).build();
        Writing writing = Writing.builder().id(UUID.randomUUID()).content(content).user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(writingRepository.save(any(Writing.class))).thenReturn(writing);

        // when
        Writing savedWriting = writingService.createWriting(userId, content);

        // then
        assertNotNull(savedWriting);
        assertEquals(content, savedWriting.getContent());
        verify(userRepository).findById(userId);
        verify(writingRepository).save(any(Writing.class));
    }

    @Test
    void createWriting_userNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        String content = "Spring Test Content";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> writingService.createWriting(userId, content));
    }
}
