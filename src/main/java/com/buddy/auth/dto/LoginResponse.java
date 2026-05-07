package com.buddy.auth.dto;

public record LoginResponse(
        String accessToken,
        String role
) {
}
