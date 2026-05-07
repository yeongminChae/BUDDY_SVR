package com.buddy.table.model;

import java.time.LocalDateTime;

public record TableAssignment(
        Long sessionId,
        Long userId,
        Integer roundNo,
        Integer tableNo,   // 해당 라운드의 최종 테이블 번호
        Integer s1GroupId, // round2에서만 의미 있음(=round1 tableNo), round1은 null
        LocalDateTime createdAt
) {

    public TableAssignment updateTableAssignment(Integer newTableNo) {
        return new TableAssignment(
                sessionId,
                userId,
                roundNo,
                newTableNo,
                s1GroupId,
                createdAt
        );
    }

}

