package com.buddy.word.model;

import java.time.LocalDateTime;

public record WordEntry(
        Long id,
        Long sessionId, // nullable allowed (MVP: allow anytime)
        Long userId,
        String phrase,
        String example, // optional
        String memo,    // optional
        LocalDateTime createdAt
) {

    // 생성 시점에 createdAt이 null이면 현재 시간을 할당
    public WordEntry {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
