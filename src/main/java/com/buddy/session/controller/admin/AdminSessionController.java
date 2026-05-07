package com.buddy.session.controller.admin;

import com.buddy.common.web.ApiResponse;
import com.buddy.session.dto.admin.session.create.CreateSessionRequest;
import com.buddy.session.dto.admin.session.create.CreateSessionResponse;
import com.buddy.session.dto.admin.session.get.GetAdminSessionListResponse;
import com.buddy.session.dto.admin.session.get.GetAdminSessionManageResponse;
import com.buddy.session.dto.admin.session_applicant.get.GetAdminParticipantManageResponse;
import com.buddy.session.dto.admin.session_candidate.create.AddSessionParticipantsRequest;
import com.buddy.session.dto.admin.session_candidate.create.AddSessionParticipantsResponse;
import com.buddy.session.dto.admin.session_candidate.get.GetAdminParticipantAddCandidateResponse;
import com.buddy.session.service.admin.AdminSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final AdminSessionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateSessionResponse>> createSession(
            @Valid @RequestBody CreateSessionRequest req
    ) {
        CreateSessionResponse response = service.createSession(req);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetAdminSessionListResponse>> getAdminSessionList() {
        GetAdminSessionListResponse response = service.getAdminSessionList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<GetAdminSessionManageResponse>> getAdminSessionManage(
            @PathVariable Long sessionId
    ) {
        GetAdminSessionManageResponse response = service.getAdminSessionManage(sessionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{sessionId}/participants")
    public ResponseEntity<ApiResponse<GetAdminParticipantManageResponse>> getAdminSessionParticipantsManage(
            @PathVariable Long sessionId
    ) {
        GetAdminParticipantManageResponse response = service.getAdminSessionParticipants(sessionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{sessionId}/participants/candidates")
    public ResponseEntity<ApiResponse<GetAdminParticipantAddCandidateResponse>> getParticipantAddCandidateItem(
            @PathVariable Long sessionId,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        GetAdminParticipantAddCandidateResponse response =
                service.getParticipantCandidates(sessionId, query, limit, offset);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{sessionId}/participants")
    public ResponseEntity<ApiResponse<AddSessionParticipantsResponse>> addSessionParticipants(
            @PathVariable Long sessionId,
            @RequestBody AddSessionParticipantsRequest request
    ) {
        AddSessionParticipantsResponse response =
                service.addSessionParticipants(sessionId, request);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{sessionId}/cancel")
    public ResponseEntity<ApiResponse<Integer>> sessionClose(
            @PathVariable Long sessionId
    ) {
        Integer response = service.closeSession(sessionId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{sessionId}/participants/{applicationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSessionParticipant(
            @PathVariable Long sessionId,
            @PathVariable Long applicationId
    ) {
        try {
            service.cancelSessionApplication(sessionId, applicationId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("CANCEL_SESSION_PARTICIPANT_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."));
        }
    }

}