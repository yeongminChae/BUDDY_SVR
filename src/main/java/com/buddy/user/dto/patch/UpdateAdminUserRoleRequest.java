package com.buddy.user.dto.patch;

import jakarta.validation.constraints.NotNull;

public record UpdateAdminUserRoleRequest(
        @NotNull(message = "역할은 필수입니다.")
        UserRole role
) {

}
