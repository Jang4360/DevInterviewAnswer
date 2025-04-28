package dev.interview.server.global.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.interview.server.global.exception.ErrorCode;
import dev.interview.server.global.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인가 예외 발생시 security 에서 잡아 403 에러 반환
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponse error = ErrorResponse.of(
                ErrorCode.FORBIDDEN,
                ErrorCode.FORBIDDEN.getMessage()
        );
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
