package com.buddy.table.controller;

import com.buddy.common.web.ApiResponse;
import com.buddy.table.dto.GetTableAssignmentsResponse;
import com.buddy.table.dto.RunTableingResponse;
import com.buddy.table.dto.update.UpdateTableAssignmentsRequest;
import com.buddy.table.dto.update.UpdateTableAssignmentsResponse;
import com.buddy.table.service.AdminTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/sessions")
public class AdminTableController {

    private final AdminTableService adminTableService;

    @PostMapping("/{sessionId}/tableing/run")
    public ResponseEntity<ApiResponse<RunTableingResponse>> run(
            @PathVariable Long sessionId
    ) {
        adminTableService.runTableing(sessionId);

        RunTableingResponse response =
                new RunTableingResponse(sessionId, "테이블 배치를 완료했습니다.");

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{sessionId}/table-assignments")
    public ResponseEntity<ApiResponse<GetTableAssignmentsResponse>> result(
            @PathVariable Long sessionId
    ) {
        GetTableAssignmentsResponse response =
                adminTableService.findTableAssignmentRows(sessionId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{sessionId}/table-assignments")
    public ResponseEntity<ApiResponse<UpdateTableAssignmentsResponse>> updateTable(
            @PathVariable Long sessionId,
            @RequestBody UpdateTableAssignmentsRequest req
    ) {
        UpdateTableAssignmentsResponse response =
                adminTableService.updateTableAssignments(sessionId, req);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
