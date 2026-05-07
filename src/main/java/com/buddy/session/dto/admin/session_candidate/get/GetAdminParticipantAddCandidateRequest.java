package com.buddy.session.dto.admin.session_candidate.get;

public record GetAdminParticipantAddCandidateRequest(
        Long sessionId,
        String query,
        Integer limit,
        Integer offset
) {
}
