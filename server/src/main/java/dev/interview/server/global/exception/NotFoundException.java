package dev.interview.server.global.exception;

// 존재하지 않는 리소스 요청 시 사용하는 예외 (404)
public class NotFoundException extends DevInterviewException{
    public NotFoundException(String detail) {
        super(ErrorCode.NOT_FOUND);
    }
}
