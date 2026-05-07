package com.buddy.session.dto.admin.session.create;

public record CreateSessionResponse(
        Long sessionId,
        String title,
        String startsAt,
        String location,
        Integer capacity,
        String topic,
        String status
) {
}
