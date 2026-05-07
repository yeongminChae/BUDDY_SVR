package com.buddy.auth.model;

import java.time.LocalDateTime;

public record Attendance(
        Long id,
        Long sessionId,
        Long userId,
        String status, // PRESENT/LATE/ABSENT
        LocalDateTime checkedInAt,
        String method, // QR/MANUAL
        LocalDateTime createdAt
) {

}
