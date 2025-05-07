package dev.interview.server.auth.service;

import dev.interview.server.auth.dto.JwtToken;
import dev.interview.server.auth.dto.LoginRequest;
import dev.interview.server.auth.dto.LoginResponse;
import dev.interview.server.auth.token.JwtTokenProvider;
import dev.interview.server.auth.util.PasswordUtil;
import dev.interview.server.global.exception.BadRequestException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.global.exception.UnauthorizedException;
import dev.interview.server.token.domain.RefreshToken;
import dev.interview.server.token.repository.RefreshTokenRepository;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 시도: email={}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 이메일: {}", request.email());
                    return new NotFoundException("존재하지 않는 이메일 입니다.");
                });

        if (!PasswordUtil.matches(request.password(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: email={}", request.email());
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        JwtToken token = jwtTokenProvider.generateTokens(user.getId());

        // RefreshToken 저장
        refreshTokenRepository.save(new RefreshToken(user.getId(),token.refreshToken()));

        log.info("로그인 성공: userId={}", user.getId());
        return new LoginResponse(user.getId(), token.accessToken(), token.refreshToken());
    }

    // 토큰 재발급
    @Transactional
    public JwtToken reissue(String refreshToken) {
        log.info("토큰 재발급 시도");
        UUID userId = jwtTokenProvider.getUserId(refreshToken);

        RefreshToken saved = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("토큰 재발급 실패 - 저장된 토큰 없음: userId={}", userId);
                    return new UnauthorizedException("리프레시 토큰이 없습니다.");
                });

        if (!saved.getToken().equals(refreshToken)) {
            log.warn("토큰 재발급 실패 - 토큰 불일치: userId={}", userId);
            throw new UnauthorizedException("토큰이 일치하지 않습니다.");
        }

        if (jwtTokenProvider.isExpired(refreshToken)) {
            log.warn("토큰 재발급 실패 - 토큰 만료: userId={}", userId);
            throw new UnauthorizedException("리프레시 토큰이 만료되었습니다.");
        }

        JwtToken newToken = jwtTokenProvider.generateTokens(userId);
        saved.updateToken(newToken.refreshToken());
        log.info("토큰 재발급 성공: userId={}", userId);

        return newToken;
    }
}
