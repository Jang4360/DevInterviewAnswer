package dev.interview.server.global.exception;

// 프론트에 내려줄 에러 포맷
public record ErrorResponse(int status, String message, String detail) {
    public static ErrorResponse of(ErrorCode code, String detail) {
        return new ErrorResponse(code.getStatus(), code.getMessage(), detail);
    }
}
