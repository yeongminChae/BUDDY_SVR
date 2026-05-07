package com.buddy.common.web;

public class ApiResponse<T> {

    private final String resultCode;
    private final String message;
    private final T data;

    private ApiResponse(String resultCode, String message, T data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("OK", "OK", data);
    }

    public static <T> ApiResponse<T> fail(String resultCode, String message) {
        return new ApiResponse<>(resultCode, message, null);
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
