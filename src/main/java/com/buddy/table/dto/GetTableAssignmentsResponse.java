package com.buddy.table.dto;

import java.util.List;

public record GetTableAssignmentsResponse(
        Long sessionId,
        List<TableAssignmentGroupResponse> round1Tables,
        List<TableAssignmentGroupResponse> round2Tables
) {
}
