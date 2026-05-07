package com.buddy.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull Long sessionId,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String phrase,
        String example
) {
}
