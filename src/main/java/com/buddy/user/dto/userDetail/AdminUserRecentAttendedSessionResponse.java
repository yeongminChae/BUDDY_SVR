package com.buddy.user.dto.userDetail;

public record AdminUserRecentAttendedSessionResponse(
        Long sessionId,
        String title,
        String startsAt,
        String location
) {
}
