package com.buddy.session.dto.admin.session_history;

public record SessionHistoryAttendanceSummaryResponse(
        Integer attendedCount,
        Integer appliedCount,
        Integer noShowCount
) {
}
