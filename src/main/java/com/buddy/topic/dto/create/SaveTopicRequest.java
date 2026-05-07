package com.buddy.topic.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveTopicRequest(
        @NotNull Long sessionId,
        @NotBlank String mainTopic,
        @NotEmpty List<@Valid SaveTopicItem> questions
) {
}
