package com.buddy.user.dto.userDetail.row;

public record AdminUserMonthlyAttendanceRow(
        Integer appliedCount,
        Integer attendedCount,
        Integer absentCount,
        Integer attendanceRate
) {
}
