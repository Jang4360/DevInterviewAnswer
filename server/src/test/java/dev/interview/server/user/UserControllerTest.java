package dev.interview.server.user;

import dev.interview.server.restdocs.RestDocsSupport;
import dev.interview.server.user.controller.UserController;
import dev.interview.server.user.domain.User;
import dev.interview.server.user.dto.UserSignupRequest;
import dev.interview.server.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(UserTestConfig.class)
@DisplayName("UserController 단위 테스트")
public class UserControllerTest extends RestDocsSupport {
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원 가입 API 성공 테스트")
    void signup_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        String name = "yoon";
        String password = "1234";
        String email = "test@naver.com";

        UserSignupRequest request = new UserSignupRequest(name,email,password);

        User savedUser = User.builder()
                .id(userId)
                .email(email)
                .build();

        when(userService.save(any(User.class))).thenReturn(savedUser);

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("signup-success",
                        requestFields(
                                fieldWithPath("name").description("사용자 이름"),   // 추가!
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("id").description("회원 ID"),
                                fieldWithPath("name").description("사용자 이름"),   // 응답에도 추가했을 경우
                                fieldWithPath("email").description("이메일")
                        )
                ))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.email").value(email));
    }
}
