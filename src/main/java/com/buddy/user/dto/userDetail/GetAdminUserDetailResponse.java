package com.buddy.user.dto.userDetail;

import java.util.List;

public record GetAdminUserDetailResponse(
        Long userId,
        String name,
        String nickname,
        String email,
        String role,
        String status,
        Integer level,
        String mbti,
        String jobs,
        AdminUserMonthlyAttendanceResponse monthlyAttendance,
        AdminUserRecentAttendedSessionResponse recentAttendedSession,
        List<AdminUserMonthlyWordResponse> monthlyWords,
        Integer badgeCount
) {
}
