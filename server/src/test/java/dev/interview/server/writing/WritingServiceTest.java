package dev.interview.server.writing;

import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.dto.WritingCreateResponse;
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

    // 글 조회 성공
    @Test
    void getWritingsByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID writingId = UUID.randomUUID();
        String content = "Spring study";

        User user = User.builder().id(userId).build();

        Writing writing = Writing.builder()
                .id(writingId)
                .content(content)
                .user(user)
                .build();

        when(writingRepository.findById(writingId)).thenReturn(Optional.of(writing));

        // when
        WritingCreateResponse response = writingService.findById(writingId);

        // then
        assertEquals(content, response.content());
        assertEquals(writingId, response.id());
        verify(writingRepository).findById(writingId);
    }

    // 글 생성 성공
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

    // 글 생성 실패
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
