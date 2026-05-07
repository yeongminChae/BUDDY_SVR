package com.buddy.user.dto.create;

public record CreateAdminUserResponse(
        Long userId,
        String email,
        String name,
        String nickname,
        String gender,
        String role,
        Integer level,
        String status,
        String jobs,
        String mbti,
        String createdAt,
        String updatedAt
) {
}
