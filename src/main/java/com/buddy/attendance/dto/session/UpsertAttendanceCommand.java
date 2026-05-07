package com.buddy.attendance.dto.session;

import com.buddy.attendance.model.enums.AttendanceMethod;
import com.buddy.attendance.model.enums.AttendanceStatus;

import java.time.LocalDateTime;

public record UpsertAttendanceCommand(
        Long sessionId,
        Long userId,
        AttendanceStatus status,
        LocalDateTime checkedInAt,
        AttendanceMethod method,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
