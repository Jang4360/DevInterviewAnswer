package dev.interview.server.auth;

import dev.interview.server.auth.dto.JwtToken;
import dev.interview.server.auth.dto.LoginRequest;
import dev.interview.server.auth.dto.LoginResponse;
import dev.interview.server.auth.service.AuthService;
import dev.interview.server.auth.token.JwtTokenProvider;
import dev.interview.server.global.exception.BadRequestException;
import dev.interview.server.global.exception.NotFoundException;
import dev.interview.server.global.exception.UnauthorizedException;
import dev.interview.server.token.domain.RefreshToken;
import dev.interview.server.token.repository.RefreshTokenRepository;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    // 로그인 성공 테스트
    @Test
    void login_success() {
        //given
        String email = "test@example.com";
        String password = "password";
        UUID userId = UUID.randomUUID();
        String encodedPassword = "encodedPassword";

        User user = User.builder()
                .id(userId)
                .email(email)
                .password(encodedPassword)
                .build();


        JwtToken token = new JwtToken("access-token", "refresh-token");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.generateTokens(userId)).thenReturn(token);

        // when
        LoginResponse response = authService.login(new LoginRequest(email, password));

        // then
        assertEquals(userId, response.userId());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // 이메일 없음 로그인 실패 테스트
    @Test
    void login_fail_emailNotFound() {
        // given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () ->
                authService.login(new LoginRequest("notfound@example.com", "password"))
        );
    }

    // 잘못된 비밀번호 로그인 실패 테스트
    @Test
    void login_fail_wrongPassword() {
        // given
        String email = "test@example.com";
        String password = "wrongPassword";
        User user = User.builder().id(UUID.randomUUID()).email(email).password("encodedPassword").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(BadRequestException.class, () ->
                authService.login(new LoginRequest(email, password))
        );
    }

    // 재발급 성공 테스트
    @Test
    void reissue_success() {
        // given
        UUID userId = UUID.randomUUID();
        String oldToken = "old-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        RefreshToken savedToken = new RefreshToken(userId, oldToken);
        JwtToken newToken = new JwtToken(newAccessToken, newRefreshToken);

        when(jwtTokenProvider.getUserId(oldToken)).thenReturn(userId);
        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(savedToken));
        when(jwtTokenProvider.isExpired(oldToken)).thenReturn(false);
        when(jwtTokenProvider.generateTokens(userId)).thenReturn(newToken);

        // when
        JwtToken result = authService.reissue(oldToken);

        // then
        assertEquals(newAccessToken, result.accessToken());
        assertEquals(newRefreshToken, result.refreshToken());
        assertEquals(newRefreshToken, savedToken.getToken());
    }

    // 토큰 재발급 토큰 없음 테스트
    @Test
    void reissue_fail_tokenNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        String refreshToken = "refresh-token";

        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(userId);
        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> authService.reissue(refreshToken));
    }

    // 토큰 재발급 refresh 토큰 검증 실패 테스트
    @Test
    void reissue_fail_tokenMismatch() {
        // given
        UUID userId = UUID.randomUUID();
        String refreshToken = "refresh-token";
        String savedTokenValue = "different-token";

        RefreshToken savedToken = new RefreshToken(userId, savedTokenValue);

        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(userId);
        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(savedToken));

        // when & then
        assertThrows(UnauthorizedException.class, () -> authService.reissue(refreshToken));
    }

    // 토큰 재발급 refresh token 만료 테스트
    @Test
    void reissue_fail_tokenExpired() {
        // given
        UUID userId = UUID.randomUUID();
        String refreshToken = "refresh-token";

        RefreshToken savedToken = new RefreshToken(userId, refreshToken);

        when(jwtTokenProvider.getUserId(refreshToken)).thenReturn(userId);
        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(savedToken));
        when(jwtTokenProvider.isExpired(refreshToken)).thenReturn(true);

        // when & then
        assertThrows(UnauthorizedException.class, () -> authService.reissue(refreshToken));
    }
}

