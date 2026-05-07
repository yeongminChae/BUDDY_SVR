package com.buddy.attendance.model;

public record AttendanceSessionRow(
        Long sessionId,
        String title,
        String startsAt,
        String location
) {
}
