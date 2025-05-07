package dev.interview.server.user.service;

import dev.interview.server.auth.util.PasswordUtil;
import dev.interview.server.global.exception.BadRequestException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// 사용자 관련 비즈니스 로직
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 저장 (회원가입)
    @Transactional
    public User save(User user) {
        log.info("회원가입 시도: email={}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("이미 사용중인 이메일 입니다.");
        }
        // 비밀번호 암호화
        user = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(PasswordUtil.encode(user.getPassword()))
                .build();
        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: userId={}", savedUser.getId());
        return savedUser;
    }

    public User getUserByIdOrThrow(UUID userId) {
        log.info("사용자 조회: userId={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 조회 실패: userId={}", userId);
                    return new NotFoundException("존재하지 않는 사용자입니다.");
                });
    }

}
