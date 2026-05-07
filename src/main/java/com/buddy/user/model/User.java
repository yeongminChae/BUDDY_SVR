package com.buddy.user.model;

import java.time.LocalDateTime;

public record User(
        Long id,
        String email,
        String passwordHash,
        String name,
        String nickname,
        String gender, // M/F (MVP)
        String role,   // USER/ADMIN
        Integer level, // 1~5
        Integer monthlyGoal, // ex) 4
        String status, // ACTIVE/DORMANT/BLOCKED
        String mbti,
        String jobs,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
