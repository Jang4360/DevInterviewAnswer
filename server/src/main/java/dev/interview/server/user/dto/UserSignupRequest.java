package dev.interview.server.user.dto;

import dev.interview.server.user.domain.User;

// 회원가입 DTO
public record UserSignupRequest (
        String name,
        String email,
        String password
){
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
    }
}
