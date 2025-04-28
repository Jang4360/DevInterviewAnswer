package dev.interview.server.global.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.interview.server.global.exception.ErrorCode;
import dev.interview.server.global.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증 예외 발생시 security 에서 잡아 401 에러 반환
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("[인증 실패] URI: {}, 메시지: {}", request.getRequestURI(), authException.getMessage());
        ErrorResponse error = ErrorResponse.of(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
