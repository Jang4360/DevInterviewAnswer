package dev.interview.server.auth.dto;

// 응답 DTO
public record JwtToken(String accessToken, String refreshToken){}
