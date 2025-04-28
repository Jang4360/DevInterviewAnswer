package dev.interview.server.auth.controller;

import dev.interview.server.auth.dto.JwtToken;
import dev.interview.server.auth.dto.LoginRequest;
import dev.interview.server.auth.dto.LoginResponse;
import dev.interview.server.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "로그인 및 토큰 관련 API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "RefreshToken 을 통해 AccessToken 을 재발급합니다.")
    public ResponseEntity<JwtToken> reissue(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.reissue(refreshToken));
    }
}
