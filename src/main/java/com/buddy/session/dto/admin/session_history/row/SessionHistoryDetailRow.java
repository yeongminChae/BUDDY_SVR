package com.buddy.session.dto.admin.session_history.row;

public record SessionHistoryDetailRow(
        Long sessionId,
        String title,
        String startsAt,
        String location,
        Integer appliedCount,
        Integer attendedCount,
        Integer noShowCount,
        Integer attendanceRate,
        String topicTitle
) {
}
