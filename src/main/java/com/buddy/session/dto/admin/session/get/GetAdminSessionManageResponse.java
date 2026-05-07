package com.buddy.session.dto.admin.session.get;

public record GetAdminSessionManageResponse(
        Long sessionId,
        String title,
        String startsAt,
        String location,
        Integer attendanceCount,
        Integer capacity,
        Boolean hasTableAssignments,
        String status
) {
}
