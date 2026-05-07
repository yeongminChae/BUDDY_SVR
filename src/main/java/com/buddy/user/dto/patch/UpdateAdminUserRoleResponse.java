package com.buddy.user.dto.patch;

public record UpdateAdminUserRoleResponse(
        Long userId,
        UserRole role
) {

}
