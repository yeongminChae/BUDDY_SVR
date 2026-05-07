package com.buddy.session.dto.admin.session_candidate.create;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddSessionParticipantsRequest(
        @NotEmpty List<Long> userIds
) {
}
