package com.buddy.user.dto.userList;

import java.util.List;

public record GetAdminUserListResponse(
        List<AdminUserListItemResponse> users,
        String query,
        Integer limit,
        Integer offset,
        Boolean hasNext,
        Integer totalUserCount
) {
}
