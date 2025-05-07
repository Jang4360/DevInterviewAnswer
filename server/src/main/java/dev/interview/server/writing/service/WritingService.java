package dev.interview.server.writing.service;

import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import dev.interview.server.user.service.UserService;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.dto.WritingCreateResponse;
import dev.interview.server.writing.repository.WritingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// 글 작성 관련 비즈니스 로직
@Service
@RequiredArgsConstructor
public class WritingService {
    private final WritingRepository writingRepository;
    private final UserService userService;

    // 사용자 글 목록 조회
    @Transactional(readOnly = true)
    public WritingCreateResponse findById(UUID id) {
        Writing writing = writingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다. ID: " + id));

        return WritingCreateResponse.from(writing);
    }

    // 글 작성 저장
    @Transactional
    public Writing createWriting(UUID userId, String content) {
        User user = userService.getUserByIdOrThrow(userId);

        Writing writing = Writing.builder()
                .content(content)
                .user(user)
                .build();

        return writingRepository.save(writing);
    }
}
