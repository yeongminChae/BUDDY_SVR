package com.buddy.user.dto.patch;

import jakarta.validation.constraints.NotNull;

public record UpdateAdminUserStatusRequest(
        @NotNull(message = "상태는 필수입니다.")
        UserStatus status
) {

}
