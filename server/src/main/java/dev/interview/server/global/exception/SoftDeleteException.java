package dev.interview.server.global.exception;

// Soft delete 된 데이터를 접근하려 할 때 발생하는 예외 (404)
public class SoftDeleteException extends DevInterviewException{
    public SoftDeleteException(String detail) {
        super(ErrorCode.NOT_FOUND);
    }
}
