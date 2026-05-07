package com.buddy.user.dto.userDetail.row;

public record AdminUserMonthlyWordRow(
        Long wordEntryId,
        Long sessionId,
        String phrase,
        String example,
        String submittedAt
) {
}

