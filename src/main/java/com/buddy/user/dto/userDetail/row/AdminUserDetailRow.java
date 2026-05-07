package com.buddy.user.dto.userDetail.row;

public record AdminUserDetailRow(
        Long userId,
        String name,
        String nickname,
        String email,
        String role,
        String status,
        Integer level,
        String mbti,
        String jobs
) {
}
