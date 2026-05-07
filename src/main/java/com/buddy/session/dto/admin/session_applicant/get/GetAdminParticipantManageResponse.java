package com.buddy.session.dto.admin.session_applicant.get;

import java.util.List;

public record GetAdminParticipantManageResponse(
        Long sessionId,
        String title,
        Integer attendanceCount,
        Integer capacity,
        List<AdminParticipantItemResponse> participants
) {
}
