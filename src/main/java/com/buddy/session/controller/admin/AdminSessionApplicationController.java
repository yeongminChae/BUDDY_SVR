package com.buddy.session.controller.admin;


import com.buddy.common.web.ApiResponse;
import com.buddy.session.service.admin.AdminSessionApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/session-applications")
@RequiredArgsConstructor
public class AdminSessionApplicationController {

    private final AdminSessionApplicationService service;

    @PatchMapping("/{applicationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSessionParticipant(
            @PathVariable Long applicationId
    ) {
        service.cancelSessionApplication(applicationId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

}