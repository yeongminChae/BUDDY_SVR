package com.buddy.session.dto.admin.session_history.row;

public record SessionHistoryMemberRow(
        Long userId,
        String name,
        String nickname
) {
}
