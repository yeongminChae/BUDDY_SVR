package com.buddy.table.dto;

import java.util.List;

public record TableAssignmentGroupResponse(
        Integer tableNo,
        List<TableMemberResponse> members
) {
}
