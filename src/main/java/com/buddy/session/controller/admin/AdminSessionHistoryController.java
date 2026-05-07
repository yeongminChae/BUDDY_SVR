package com.buddy.session.controller.admin;

import com.buddy.common.web.ApiResponse;
import com.buddy.session.dto.admin.session_history.GetAdminSessionHistoryDetailResponse;
import com.buddy.session.service.admin.AdminSessionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sessions/history")
@RequiredArgsConstructor
public class AdminSessionHistoryController {

    private final AdminSessionHistoryService service;

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<GetAdminSessionHistoryDetailResponse>>
    getAdminSessionHistoryDetail(
            @PathVariable Long sessionId
    ) {
        GetAdminSessionHistoryDetailResponse response
                = service.getAdminSessionHistoryDetail(sessionId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}