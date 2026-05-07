package com.buddy.attendance.model;

import java.time.LocalDateTime;

public record AttendanceWord(
        Long id,
        Long sessionId,
        Long userId,
        String phrase,
        String example,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
