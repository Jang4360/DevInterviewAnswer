package dev.interview.server.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 비밀번호 암호화 관련 유틸리티 클래스
public class PasswordUtil {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 암호화
    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 비밀번호 일치 여부 검사
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
