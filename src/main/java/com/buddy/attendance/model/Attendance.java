package com.buddy.attendance.model;

import com.buddy.attendance.model.enums.AttendanceMethod;
import com.buddy.attendance.model.enums.AttendanceStatus;

import java.time.LocalDateTime;

public record Attendance(
        Long id,
        Long sessionId,
        Long userId,
        AttendanceStatus status, // PRESENT/LATE/ABSENT
        LocalDateTime checkedInAt,
        AttendanceMethod method, // QR/MANUAL
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {

}
