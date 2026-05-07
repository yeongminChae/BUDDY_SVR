package com.buddy.session.dto.admin.session_history;

import java.util.List;

public record SessionHistoryTableRoundResponse(
        Integer roundNo,
        List<SessionHistoryTableResponse> tables
) {
}
