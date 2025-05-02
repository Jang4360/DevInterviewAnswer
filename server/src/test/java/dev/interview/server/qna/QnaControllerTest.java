package dev.interview.server.qna;

import dev.interview.server.qna.controller.QnaController;
import dev.interview.server.qna.domain.Qna;
import dev.interview.server.qna.dto.QnaCreateRequest;
import dev.interview.server.qna.dto.QnaTodayResponse;
import dev.interview.server.qna.service.QnaService;
import dev.interview.server.restdocs.RestDocsSupport;
import dev.interview.server.user.domain.User;
import dev.interview.server.writing.domain.Writing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QnaController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(QnaTestConfig.class)
@DisplayName("QnaController 단위 테스트")
public class QnaControllerTest extends RestDocsSupport {
    @Autowired
    private QnaService qnaService;

    @Test
    @DisplayName("GPT 질문저장 API 성공테스트")
    void createQna_success() throws Exception {
        //given
        UUID writingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String question = "Spring 이란?";
        String answer = "Spring 은 자바 웹 프레임워크입니다.";

        QnaCreateRequest request = new QnaCreateRequest(userId, question, answer);

        Qna saved = Qna.builder()
                .id(UUID.randomUUID())
                .user(User.builder().id(userId).build())
                .writing(Writing.builder().id(writingId).build())
                .question(question)
                .answer(answer)
                .scheduledDate(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(qnaService.saveQna(eq(userId), eq(writingId), eq(question), eq(answer), any(LocalDateTime.class)))
                .thenReturn(saved);

        // when & then
        mockMvc.perform(post("/api/qna/{writingId}", writingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(document("qna-create-success",
                        pathParameters(
                                parameterWithName("writingId").description("작성 글 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("question").description("질문 내용"),
                                fieldWithPath("answer").description("질문에 대한 답변")
                        ),
                        responseFields(
                                fieldWithPath("qnaId").description("저장된 QnA ID")
                        )
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qnaId").value(saved.getId().toString()));
    }

    @Test
    @DisplayName("오늘 복습할 질문 조회 API 성공테스트")
    void getTodayReviewQnas_success() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID qnaId = UUID.randomUUID();
        String question = "Spring Boot란?";
        String answer = "Spring Boot는 자바 기반 웹 프레임워크입니다.";
        LocalDateTime scheduleDate = LocalDateTime.now().minusDays(1);

        List<QnaTodayResponse> mockResponse = List.of(
                new QnaTodayResponse(qnaId,question,answer,scheduleDate)
        );

        when(qnaService.getReviewQnasForToday(userId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/qna/today")
                        .param("userId",userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(document("qna-today-review-success",
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("QnA ID"),
                                fieldWithPath("[].question").description("질문 내용"),
                                fieldWithPath("[].answer").description("답변 내용"),
                                fieldWithPath("[].scheduleDate").description("예정된 복습 날짜")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(qnaId.toString()))
                .andExpect(jsonPath("$[0].question").value(question))
                .andExpect(jsonPath("$[0].answer").value(answer));

    }

    @Test
    @DisplayName("사용자별 전체 질문 조회 API 성공 테스트")
    void getQnaListByUser_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID qnaId = UUID.randomUUID();
        String question = "JPA?";
        LocalDateTime scheduleTime = LocalDateTime.now().plusDays(3);

        Qna qna = Qna.builder()
                .id(qnaId)
                .question(question)
                .scheduledDate(scheduleTime)
                .isDeleted(false)
                .user(User.builder().id(UUID.randomUUID()).build())
                .build();

        when(qnaService.getAllByUserId(userId)).thenReturn(List.of(qna));

        // when & then
        mockMvc.perform(get("/api/qna/user/{userID}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(document("qna-user-list-success",
                        pathParameters(
                                parameterWithName("userID").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("QnA ID"),
                                fieldWithPath("[].question").description("질문 내용"),
                                fieldWithPath("[].scheduleDate").description("예정된 복습 날짜"),
                                fieldWithPath("[].isDeleted").description("삭제 여부")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(qnaId.toString()))
                .andExpect(jsonPath("$[0].question").value(question))
                .andExpect(jsonPath("$[0].scheduleDate").exists())
                .andExpect(jsonPath("$[0].isDeleted").value(false));
    }

    @Test
    @DisplayName("질문 삭제 API 성공 테스트")
    void deleteQna_success() throws Exception {
        // given
        UUID qnaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doNothing().when(qnaService).deleteQna(qnaId,userId);

        // when & then
        mockMvc.perform(delete("/api/qna/{qnaId}?userId={userID}", qnaId, userId))
                .andDo(document("qna-delete-success",
                        pathParameters(
                                parameterWithName("qnaId").description("QnA ID")
                        ),
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID")
                        )
                ))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("질문 단건 조회 API 성공 테스트")
    void getQnaById_success() throws Exception {
        // given
        UUID qnaId = UUID.randomUUID();
        Qna qna = Qna.builder()
                .id(qnaId)
                .question("Spring이란?")
                .answer("Java 기반 프레임워크")
                .build();

        when(qnaService.findById(qnaId.toString())).thenReturn(qna);

        // when & then
        mockMvc.perform(get("/api/qna/{id}", qnaId))
                .andDo(document("qna-get-success",
                        pathParameters(
                                parameterWithName("id").description("QnA ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("QnA ID"),
                                fieldWithPath("question").description("질문 내용"),
                                fieldWithPath("answer").description("답변 내용"),
                                fieldWithPath("lastReviewedAt").optional().description("마지막 복습 일시"),
                                fieldWithPath("scheduledDate").optional().description("다음 복습 예정 일자"),
                                fieldWithPath("deleted").description("삭제 여부"),
                                fieldWithPath("reviewed").description("복습 완료 여부"),
                                fieldWithPath("writing").description("작성한 글 객체").optional(),
                                fieldWithPath("user").description("작성자 객체").optional()
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(qnaId.toString()))
                .andExpect(jsonPath("$.question").value("Spring이란?"))
                .andExpect(jsonPath("$.answer").value("Java 기반 프레임워크"));
    }

    @Test
    @DisplayName("누적 질문 수 조회 API 성공 테스트")
    void getQuestionCount_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        Long count = 5L;

        when(qnaService.countByUser(userId)).thenReturn(count);

        // when & then
        mockMvc.perform(get("/api/qna/user/{userId}/count", userId))
                .andDo(document("qna-count-success",
                        pathParameters(
                                parameterWithName("userId").description("사용자 ID")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

}
