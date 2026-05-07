package com.buddy.session.dto.admin.session_history;

import java.util.List;

public record SessionHistoryTableResponse(
        String tableName,
        List<String> members
) {
}
