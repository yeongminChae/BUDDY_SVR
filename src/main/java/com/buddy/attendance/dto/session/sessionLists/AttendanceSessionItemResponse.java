package com.buddy.attendance.dto.session.sessionLists;


// 20260505 미사용
public record AttendanceSessionItemResponse(
        Long sessionId,
        String title,
        String startsAt,
        String location
) {
}
