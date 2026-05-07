package com.buddy.attendance.model;

public record AttendanceUserRow(
        Long userId,
        String name,
        String nickname
) {
}
