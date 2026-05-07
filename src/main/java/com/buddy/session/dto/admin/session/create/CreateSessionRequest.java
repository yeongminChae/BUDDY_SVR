package com.buddy.session.dto.admin.session.create;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSessionRequest(

        @NotBlank(message = "세션명은 필수입니다.")
        String title,

        @NotNull(message = "날짜는 필수입니다.")
        @FutureOrPresent(message = "날짜는 오늘 이후여야 합니다.")
        LocalDate sessionDate,

        @NotNull(message = "시간은 필수입니다.")
        LocalTime sessionTime,

        @NotBlank(message = "장소는 필수입니다.")
        String location,

        @NotNull(message = "정원은 필수입니다.")
        @Min(value = 2, message = "정원은 최소 2명 이상이어야 합니다.")
        @Max(value = 100, message = "정원은 최대 100명까지 가능합니다.")
        Integer capacity,

        // @NotBlank(message = "주제는 필수입니다.")
        String topic,

        @NotNull(message = "호스트는 필수입니다.")
        Long hostUserId

) {
}
