package dev.interview.server.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// 프로젝트 전역에서 발생하는 예외들을 처리
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 커스텀 예외 처리: DevInterviewException 상속 클래스
    @ExceptionHandler(DevInterviewException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(DevInterviewException ex){
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.of(code, ex.getMessage()));
    }

    // @Valid 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ErrorCode.VALIDATION_ERROR, detail));
    }

    // @ModelAttribute 바인딩 실패 처리
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        String detail = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ErrorCode.VALIDATION_ERROR, detail));
    }

    // 처리되지 않은 모든 예외 처리 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception ex) {
        log.error("[예외 발생] 서버오류: ", ex); // 로그만 남김
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다."));
    }
}
