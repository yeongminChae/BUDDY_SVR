package com.buddy.user.dto.userList;

public record AdminUserListRow(
        Long userId,
        String name,
        String nickname,
        Integer level,
        String recentSession,
        String recentSessionTitle,
        String status,
        String role
) {
}
