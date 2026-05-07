package com.buddy.session.dto.admin.session_history.row;

public record SessionHistoryParticipantRow(
        Long userId,
        String name,
        String nickname
) {
}
