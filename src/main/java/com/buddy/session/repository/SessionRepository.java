package com.buddy.session.repository;

import com.buddy.attendance.dto.session.row.AttendanceSessionDetailRow;
import com.buddy.session.dto.admin.session_applicant.get.AdminParticipantItemResponse;
import com.buddy.session.dto.admin.session_candidate.get.AdminParticipantAddCandidateItemResponse;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryDetailRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryParticipantRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryTableMemberRow;
import com.buddy.session.model.Session;
import com.buddy.session.model.SessionApplication;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository {

    List<Session> findUpcoming();

    List<Session> findPast();

    void upsertApplication(Long sessionId, Long userId, String status);

    Session findSessionById(Long sessionId);

    int countTableAssignmentsBySessionId(Long sessionId);

    List<SessionApplication> findApplications(Long sessionId);

    Integer insertSession(Session session);

    List<AdminParticipantItemResponse> findSessionParticipants(Long sessionId);

    int cancelSessionApplication(
            Long applicationId,
            Long sessionId,
            LocalDateTime updatedAt
    );

    List<AdminParticipantAddCandidateItemResponse> getParticipantCandidates(
            Long sessionId,
            String query,
            Integer limit,
            Integer offset
    );

    int insertSessionParticipants(
            Long sessionId,
            List<Long> userIds,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    );

    int closeSession(Long sessionId);

    List<Long> findClosableSessionIds(LocalDateTime now);

    SessionHistoryDetailRow getSessionHistoryDetail(Long sessionId);

    List<SessionHistoryParticipantRow> getSessionHistoryParticipants(Long sessionId);

    List<SessionHistoryTableMemberRow> getSessionHistoryTableMembers(Long sessionId);

    List<SessionHistoryParticipantRow> getSessionHistoryApplicants(Long sessionId);

    SessionApplication findApplicationById(Long applicationId);

    AttendanceSessionDetailRow getAttendanceSessionInfo(Long sessionId);
}

