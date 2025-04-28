package dev.interview.server.global.exception;

public class UnauthorizedException extends DevInterviewException{
    public UnauthorizedException(String detail) {
        super(ErrorCode.UNAUTHORIZED);
    }
}
