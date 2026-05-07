package com.buddy.session.dto.admin.session.get;

public record AdminSessionListItemResponse(
        Long sessionId,
        String title,
        String startsAt,
        String location,
        Integer attendanceCount,
        Integer capacity,
        String status
) {
}
