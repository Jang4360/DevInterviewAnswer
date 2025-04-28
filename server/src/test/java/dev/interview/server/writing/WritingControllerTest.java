package dev.interview.server.writing;

import dev.interview.server.restdocs.RestDocsSupport;
import dev.interview.server.user.domain.User;
import dev.interview.server.writing.controller.WritingController;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.dto.WritingCreateRequest;
import dev.interview.server.writing.service.WritingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.UUID;

// WritingController 테스트
@WebMvcTest(controllers = WritingController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(WritingTestConfig.class)
@DisplayName("WritingController 단위 테스트")
public class WritingControllerTest extends RestDocsSupport {
    @Autowired
    private WritingService writingService;

    @Test
    @DisplayName("글 생성 API 성공 테스트")
    void createWriting_success() throws Exception{
        // given
        UUID userId = UUID.randomUUID();
        String content = "Spring 정리";

        WritingCreateRequest request = new WritingCreateRequest(userId, content);

        Writing saved = Writing.builder()
                .id(UUID.randomUUID())
                .user(User.builder().id(userId).build())
                .content(content)
                .build();

        when(writingService.createWriting(userId, content)).thenReturn(saved);

        // when & then
        mockMvc.perform(post("/api/writings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("writing-create-success",
                        requestFields(
                                fieldWithPath("userId").description("유저 ID"),
                                fieldWithPath("content").description("글 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").description("글 ID"),
                                fieldWithPath("content").description("글 내용"),
                                fieldWithPath("userId").description("유저 ID")
                        )
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content").value(content));
    }
}
