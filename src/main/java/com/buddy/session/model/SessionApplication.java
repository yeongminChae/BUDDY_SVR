package com.buddy.session.model;

import java.time.LocalDateTime;

public record SessionApplication(
        Long id,
        Long sessionId,
        Long userId,
        String status, // APPLIED/CONFIRMED/CANCELED/WAITLIST
        LocalDateTime appliedAt,
        LocalDateTime updatedAt
) {

}
