package edu.istad.jomnorncode.exception;

public class FileUploadException extends RuntimeException {

    private String errorCode;

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUploadException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
