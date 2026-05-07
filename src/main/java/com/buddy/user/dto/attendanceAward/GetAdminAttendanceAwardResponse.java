package com.buddy.user.dto.attendanceAward;

import java.util.List;

public record GetAdminAttendanceAwardResponse(
        Integer year,
        Integer month,
        AttendanceAwardSummaryResponse summary,
        List<AttendanceAwardCandidateResponse> candidates
) {
}

