package com.buddy.table.service;

import com.buddy.table.dto.GetTableAssignmentsResponse;
import com.buddy.table.dto.update.UpdateTableAssignmentsRequest;
import com.buddy.table.dto.update.UpdateTableAssignmentsResponse;

public interface AdminTableService {
    void runTableing(Long sessionId);

    GetTableAssignmentsResponse findTableAssignmentRows(Long sessionId);

    UpdateTableAssignmentsResponse updateTableAssignments(
            Long sessionId, UpdateTableAssignmentsRequest request
    );
}
