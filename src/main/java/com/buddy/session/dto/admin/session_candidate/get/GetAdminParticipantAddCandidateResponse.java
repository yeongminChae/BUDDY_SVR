package com.buddy.session.dto.admin.session_candidate.get;

import java.util.List;

public record GetAdminParticipantAddCandidateResponse(
        Long sessionId,
        String query,
        Integer limit,
        Boolean hasNext,
        List<AdminParticipantAddCandidateItemResponse> candidates
) {
}
