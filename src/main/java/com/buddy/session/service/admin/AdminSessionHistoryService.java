package com.buddy.session.service.admin;

import com.buddy.session.dto.admin.session_history.GetAdminSessionHistoryDetailResponse;

public interface AdminSessionHistoryService {

    GetAdminSessionHistoryDetailResponse getAdminSessionHistoryDetail(Long sessionId);

}
