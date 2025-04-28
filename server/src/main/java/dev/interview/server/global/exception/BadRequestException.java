package dev.interview.server.global.exception;

// 잘못된 요청이나 입력값 유효성 위반시 발생하는 예외 (400)
public class BadRequestException extends DevInterviewException{
    public BadRequestException(String detail) {
        super(ErrorCode.BAD_REQUEST);
    }
}
