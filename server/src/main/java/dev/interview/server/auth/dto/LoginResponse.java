package dev.interview.server.auth.dto;

import java.util.UUID;

//로그인 응답 DTO
public record LoginResponse (UUID userId, String accessToken, String refreshToken){}
