package dev.interview.server.auth.service;

import dev.interview.server.auth.dto.JwtToken;
import dev.interview.server.auth.dto.LoginRequest;
import dev.interview.server.auth.dto.LoginResponse;
import dev.interview.server.auth.token.JwtTokenProvider;
import dev.interview.server.global.exception.BadRequestException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.global.exception.UnauthorizedException;
import dev.interview.server.token.domain.RefreshToken;
import dev.interview.server.token.repository.RefreshTokenRepository;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일 입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        JwtToken token = jwtTokenProvider.generateTokens(user.getId());

        // RefreshToken 저장
        refreshTokenRepository.save(new RefreshToken(user.getId(),token.refreshToken()));

        return new LoginResponse(user.getId(), token.accessToken(), token.refreshToken());
    }

    // 토큰 재발급
    @Transactional
    public JwtToken reissue(String refreshToken) {
        UUID userId = jwtTokenProvider.getUserId(refreshToken);

        RefreshToken saved = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException("리프레시 토큰이 없습니다."));

        if (!saved.getToken().equals(refreshToken)) {
            throw new UnauthorizedException("토큰이 일치하지 않습니다.");
        }

        if (jwtTokenProvider.isExpired(refreshToken)) {
            throw new UnauthorizedException("리프레시 토큰이 만료되었습니다.");
        }

        JwtToken newToken = jwtTokenProvider.generateTokens(userId);
        saved.updateToken(newToken.refreshToken());

        return newToken;
    }
}
