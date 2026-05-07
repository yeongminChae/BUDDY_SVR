package com.buddy.user.dto.userDetail;

public record AdminUserMonthlyWordResponse(
        Long wordEntryId,
        Long sessionId,
        String phrase,
        String example,
        String submittedAt
) {
}

