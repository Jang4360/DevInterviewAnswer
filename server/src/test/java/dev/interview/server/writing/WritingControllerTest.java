package dev.interview.server.writing;

import dev.interview.server.restdocs.RestDocsSupport;
import dev.interview.server.user.domain.User;
import dev.interview.server.writing.controller.WritingController;
import dev.interview.server.writing.domain.Writing;
import dev.interview.server.writing.dto.WritingCreateRequest;
import dev.interview.server.writing.dto.WritingCreateResponse;
import dev.interview.server.writing.service.WritingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @DisplayName("글 단건 조회 API 테스트")
    @Test
    void getWriting_success() throws Exception {
        // given
        UUID writingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        WritingCreateResponse response = new WritingCreateResponse(writingId,"작성한 글입니다.",userId);

        when(writingService.findById(writingId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/writings/{id}", writingId))
                .andDo(document("writing-get-success",
                        pathParameters(
                                parameterWithName("id").description("글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("글 ID"),
                                fieldWithPath("content").description("글 내용"),
                                fieldWithPath("userId").description("작성한 사용자")

                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(writingId.toString()))
                .andExpect(jsonPath("$.content").value("작성한 글입니다."))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

    }

}
