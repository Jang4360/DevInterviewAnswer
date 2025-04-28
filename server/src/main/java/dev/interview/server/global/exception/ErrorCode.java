package dev.interview.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 상태코드 & 메시지 정의
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    VALIDATION_ERROR(400, "요청값이 유효하지 않습니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");
    private final int status;
    private final String message;
}
