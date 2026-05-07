package com.buddy.attendance.dto;

public record CheckInResponse(
        Long attendanceId,
        Long attendanceWordId,
        Long sessionId,
        Long userId,
        String name,
        String nickname,
        String word,
        String status,
        String checkedInAt
) {

}
