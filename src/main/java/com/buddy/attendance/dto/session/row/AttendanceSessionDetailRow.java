package com.buddy.attendance.dto.session.row;

public record AttendanceSessionDetailRow(
        Long sessionId,
        String title,
        String startsAt,
        String location,
        String topicTitle
) {
}
