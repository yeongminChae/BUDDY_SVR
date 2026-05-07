package com.buddy.user.dto.userDetail;

public record AdminUserMonthlyAttendanceResponse(
        Integer appliedCount,
        Integer attendedCount,
        Integer absentCount,
        Integer attendanceRate
) {
}
