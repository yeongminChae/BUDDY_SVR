package com.buddy.table.model;

public record RoundAssignmentRow(
        Long sessionId,
        Long userId,
        Integer s1GroupId,
        String gender,
        Integer level
) {
}
