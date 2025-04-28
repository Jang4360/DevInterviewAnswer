package dev.interview.server.auth;

import dev.interview.server.auth.controller.AuthController;
import dev.interview.server.auth.dto.JwtToken;
import dev.interview.server.auth.dto.LoginRequest;
import dev.interview.server.auth.dto.LoginResponse;
import dev.interview.server.auth.service.AuthService;
import dev.interview.server.restdocs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(AuthTestConfig.class)
@DisplayName("AuthController 단위 테스트")
public class AuthControllerTest extends RestDocsSupport {
    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("로그인 API 성공 테스트")
    void login_success() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        String email = "abc.com";
        String password = "1234";
        String accessToken = "access";
        String refreshToken = "refresh";

        LoginRequest request = new LoginRequest(email, password);
        LoginResponse response = new LoginResponse(userId, accessToken, refreshToken);

        when(authService.login(request)).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("login-success",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("accessToken").description("엑세스 토큰"),
                                fieldWithPath("refreshToken").description("리프레시 토큰")
                        )
                        ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("토큰 재발급 API 성공 테스트")
    void reissue_success() throws Exception {
        // given
        String oldRefreshToken = "old-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-access-token";

        JwtToken newToken = new JwtToken(newAccessToken, newRefreshToken);

        when(authService.reissue(oldRefreshToken)).thenReturn(newToken);

        // when & then
        mockMvc.perform(post("/api/auth/reissue?refreshToken="+oldRefreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("reissue-success",
                        queryParameters(
                                parameterWithName("refreshToken").description("기존 리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("새로운 엑세스 토큰"),
                                fieldWithPath("refreshToken").description("새로운 리프레시 토큰")
                        )
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                .andExpect(jsonPath("$.refreshToken").value(newRefreshToken));
    }
}
