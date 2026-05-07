package com.buddy.table.dto;

public record TableAssignmentRow(
        Long sessionId,
        Integer roundNo,
        Integer tableNo,
        Long userId,
        String name,
        String nickname,
        String email,
        Integer level
) {
}
