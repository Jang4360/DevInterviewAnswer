package dev.interview.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 모든 커스텀 예외의 상위 클래스
@Getter
public abstract class DevInterviewException extends RuntimeException {
    private final ErrorCode errorCode;

    protected DevInterviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
