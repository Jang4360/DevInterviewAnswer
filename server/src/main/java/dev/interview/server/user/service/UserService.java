package dev.interview.server.user.service;

import dev.interview.server.global.exception.BadRequestException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

// 사용자 관련 비즈니스 로직
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 저장 (회원가입)
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("이미 사용중인 이메일 입니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(encodedPassword)
                .build();
        return userRepository.save(user);
    }

}
