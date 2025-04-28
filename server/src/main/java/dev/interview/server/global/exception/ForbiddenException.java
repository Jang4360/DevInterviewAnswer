package dev.interview.server.global.exception;

// 다른 사용자의 리소스를 접근하려 할 때 발생하는 예외 (403)
public class ForbiddenException extends DevInterviewException{
    public ForbiddenException(String detail) {
        super(ErrorCode.FORBIDDEN);
    }
}
