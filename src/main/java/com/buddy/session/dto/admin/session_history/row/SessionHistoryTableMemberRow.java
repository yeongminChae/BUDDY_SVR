package com.buddy.session.dto.admin.session_history.row;

public record SessionHistoryTableMemberRow(
        Integer roundNo,
        Integer tableNo,
        Long userId,
        String name,
        String nickname
) {
}
