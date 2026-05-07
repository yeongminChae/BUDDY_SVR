package com.buddy.session.service.admin;

import com.buddy.session.dto.admin.session.create.CreateSessionRequest;
import com.buddy.session.dto.admin.session.create.CreateSessionResponse;
import com.buddy.session.dto.admin.session.get.GetAdminSessionListResponse;
import com.buddy.session.dto.admin.session.get.GetAdminSessionManageResponse;
import com.buddy.session.dto.admin.session_applicant.get.GetAdminParticipantManageResponse;
import com.buddy.session.dto.admin.session_candidate.create.AddSessionParticipantsRequest;
import com.buddy.session.dto.admin.session_candidate.create.AddSessionParticipantsResponse;
import com.buddy.session.dto.admin.session_candidate.get.GetAdminParticipantAddCandidateResponse;

public interface AdminSessionService {
    CreateSessionResponse createSession(CreateSessionRequest req);

    GetAdminParticipantManageResponse getAdminSessionParticipants(Long sessionId);

    GetAdminSessionListResponse getAdminSessionList();

    GetAdminSessionManageResponse getAdminSessionManage(Long sessionId);

    GetAdminParticipantAddCandidateResponse getParticipantCandidates(
            Long sessionId,
            String query,
            Integer limit,
            Integer offset
    );

    AddSessionParticipantsResponse addSessionParticipants(
            Long sessionId,
            AddSessionParticipantsRequest request
    );

    int closeSession(Long sessionId);

    void cancelSessionApplication(Long sessionId, Long applicationId);

}
