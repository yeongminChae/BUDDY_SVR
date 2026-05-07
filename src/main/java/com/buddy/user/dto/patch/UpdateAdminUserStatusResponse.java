package com.buddy.user.dto.patch;

public record UpdateAdminUserStatusResponse(
        Long userId,
        UserStatus status
) {
}
