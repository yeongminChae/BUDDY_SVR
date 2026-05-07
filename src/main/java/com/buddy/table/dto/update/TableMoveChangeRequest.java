package com.buddy.table.dto.update;

public record TableMoveChangeRequest(
        Long userId,
        Integer roundNo,
        Integer fromTableNo,
        Integer toTableNo
) {
}

