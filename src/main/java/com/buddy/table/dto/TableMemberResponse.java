package com.buddy.table.dto;

public record TableMemberResponse(
        Long userId,
        String name,
        String nickname,
        String email,
        Integer level
) {
}
