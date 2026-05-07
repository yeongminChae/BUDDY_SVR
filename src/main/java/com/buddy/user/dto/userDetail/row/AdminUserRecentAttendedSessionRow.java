package com.buddy.user.dto.userDetail.row;

public record AdminUserRecentAttendedSessionRow(
        Long sessionId,
        String title,
        String startsAt,
        String location
) {
}
