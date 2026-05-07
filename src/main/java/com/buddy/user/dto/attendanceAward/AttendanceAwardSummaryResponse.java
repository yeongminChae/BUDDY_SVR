package com.buddy.user.dto.attendanceAward;

public record AttendanceAwardSummaryResponse(
        Integer totalSessionCount,
        Integer totalAppliedCount,
        Integer totalAttendedCount,
        Integer totalNoShowCount,
        Integer averageAttendanceRate
) {
}
