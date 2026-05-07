package com.buddy.table.dto.update;

import java.util.List;

public record UpdateTableAssignmentsRequest(
        List<TableMoveChangeRequest> changes
) {
}
