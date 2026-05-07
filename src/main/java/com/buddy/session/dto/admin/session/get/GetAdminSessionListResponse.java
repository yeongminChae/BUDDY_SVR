package com.buddy.session.dto.admin.session.get;

import java.util.List;

public record GetAdminSessionListResponse(
        List<AdminSessionListItemResponse> upcomingSessions,
        List<AdminSessionListItemResponse> pastSessions
) {
}
