package com.buddy.session.dto.admin.session_history;

public record SessionHistoryParticipantResponse(
        Long userId,
        String name,
        String nickname
) {
}
