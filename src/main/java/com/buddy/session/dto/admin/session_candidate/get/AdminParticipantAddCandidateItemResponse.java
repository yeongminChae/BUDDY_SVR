package com.buddy.session.dto.admin.session_candidate.get;

public record AdminParticipantAddCandidateItemResponse(
        Long userId,
        String name,
        String nickname,
        String email,
        String gender,
        Integer level,
        String jobs,
        String mbti
) {
}
