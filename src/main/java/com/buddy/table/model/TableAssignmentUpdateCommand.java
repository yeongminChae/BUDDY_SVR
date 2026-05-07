package com.buddy.table.model;

public record TableAssignmentUpdateCommand(
        Long sessionId,
        Long userId,
        Integer roundNo,
        Integer tableNo
) {
}
