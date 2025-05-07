package dev.interview.server.user.dto;

import dev.interview.server.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 회원가입 DTO
public record UserSignupRequest (
        @NotBlank String name,
        @Email String email,
        @Size(min = 8, max = 20) String password
){
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
    }
}
