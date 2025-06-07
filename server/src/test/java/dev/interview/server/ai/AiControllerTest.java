package dev.interview.server.ai;

import dev.interview.server.ai.controller.AiController;
import dev.interview.server.ai.dto.GenerateQuestionRequest;
import dev.interview.server.ai.dto.GeneratedQnaResponse;
import dev.interview.server.ai.gpt.GptClient;
import dev.interview.server.restdocs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AiTestConfig.class)
@DisplayName("AIController API 테스트")
public class AiControllerTest extends RestDocsSupport {

    @Autowired
    private GptClient gptClient;

    @WithMockUser
    @Test
    @DisplayName("질문 생성 API 성공 테스트")
    void generatedQuestions_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        String content = "spring boot 에 대해 공부했습니다";

        GenerateQuestionRequest request = new GenerateQuestionRequest(userId, content);

        GeneratedQnaResponse response = new GeneratedQnaResponse(List.of(
                new GeneratedQnaResponse.QnaItem("Spring이란?", "자바 프레임워크입니다."),
                new GeneratedQnaResponse.QnaItem("DI란?", "객체 간의 결합도를 낮추는 설계입니다.")
        ));

        // test용 config에 등록된 gptClient mock이 사용됨
//        when(gptClient.generateQuestions(any(), any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/ai/generate-questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qnaList[0].question").value("Spring이란?"))
                .andExpect(jsonPath("$.qnaList[0].answer").value("자바 프레임워크입니다."));
    }
}


