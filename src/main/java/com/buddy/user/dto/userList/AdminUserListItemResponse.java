package com.buddy.user.dto.userList;

public record AdminUserListItemResponse(
        Long userId,
        String name,
        String nickname,
        Integer level,
        Integer attendanceRate,
        String recentSession,
        String recentSessionTitle,
        String status,
        String role
) {
}
