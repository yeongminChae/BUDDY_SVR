package com.buddy.user.dto.attendanceAward;

public record AttendanceAwardCandidateResponse(
        Integer rank,
        Long userId,
        String name,
        String nickname,
        Integer level,
        Integer appliedCount,
        Integer attendedCount,
        Integer noShowCount,
        Integer attendanceRate
) {
}