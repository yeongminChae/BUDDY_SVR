package com.buddy.session.dto.admin.session_applicant.get;

public record AdminParticipantItemResponse(
        Long userId,
        Long applicationId,
        String name,
        String nickname,
        String email,
        String applicationStatus,
        String createdAt
) {
}
