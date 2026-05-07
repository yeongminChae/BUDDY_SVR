package com.buddy.user.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAdminUserRequest(
        // @NotBlank String email, TODO : 일시적 주석처리, 추후 로그인 기능 구현 시 추가 예정
        String email,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String gender,
        @NotBlank String role,
        @NotNull @Min(1) @Max(5) Integer level,
        @NotBlank String status,
        String jobs,
        String mbti
) {
}
