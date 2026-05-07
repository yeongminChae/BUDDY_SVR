package com.buddy.session.dto.admin.session_candidate.create;

public record AddSessionParticipantsResponse(
        Long sessionId,
        Integer addedUserCount
) {
}
