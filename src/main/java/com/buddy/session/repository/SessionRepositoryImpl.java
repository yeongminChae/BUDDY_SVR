package com.buddy.session.repository;

import com.buddy.attendance.dto.session.row.AttendanceSessionDetailRow;
import com.buddy.session.dto.admin.session_applicant.get.AdminParticipantItemResponse;
import com.buddy.session.dto.admin.session_candidate.get.AdminParticipantAddCandidateItemResponse;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryDetailRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryParticipantRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryTableMemberRow;
import com.buddy.session.mapper.SessionMapper;
import com.buddy.session.model.Session;
import com.buddy.session.model.SessionApplication;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final SessionMapper mapper;

    public SessionRepositoryImpl(SessionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Session> findUpcoming() {
        return mapper.findUpcoming();
    }

    @Override
    public List<Session> findPast() {
        return mapper.findPast();
    }

    @Override
    public Session findSessionById(Long sessionId) {
        return mapper.findSessionById(sessionId);
    }

    @Override
    public int countTableAssignmentsBySessionId(Long sessionId) {
        return mapper.countTableAssignmentsBySessionId(sessionId);
    }

    @Override
    public void upsertApplication(Long sessionId, Long userId, String status) {
        mapper.upsertApplication(sessionId, userId, status);
    }

    @Override
    public List<SessionApplication> findApplications(Long sessionId) {
        return mapper.findApplications(sessionId);
    }

    @Override
    public Integer insertSession(Session session) {
        return mapper.insertSession(session);
    }

    @Override
    public List<AdminParticipantItemResponse> findSessionParticipants(Long sessionId) {
        return mapper.findSessionParticipants(sessionId);
    }

    @Override
    public int cancelSessionApplication(
            Long applicationId,
            Long sessionId,
            LocalDateTime updatedAt
    ) {
        return mapper.cancelSessionApplication(applicationId, sessionId, updatedAt);
    }

    @Override
    public List<AdminParticipantAddCandidateItemResponse> getParticipantCandidates(
            Long sessionId, String query, Integer limit, Integer offset
    ) {
        return mapper.getParticipantCandidates(sessionId, query, limit, offset);
    }

    @Override
    public int insertSessionParticipants(
            Long sessionId,
            List<Long> userIds,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        return mapper.addSessionParticipants(
                sessionId,
                userIds,
                status,
                createdAt,
                updatedAt
        );
    }

    @Override
    public int closeSession(Long sessionId) {
        return mapper.closeSessions(sessionId);
    }

    @Override
    public List<Long> findClosableSessionIds(LocalDateTime now) {
        return mapper.findClosableSessionIds(now);
    }

    @Override
    public SessionHistoryDetailRow getSessionHistoryDetail(Long sessionId) {
        return mapper.getSessionHistoryDetail(sessionId);
    }

    @Override
    public List<SessionHistoryParticipantRow> getSessionHistoryParticipants(Long sessionId) {
        return mapper.getSessionHistoryParticipants(sessionId);
    }

    @Override
    public List<SessionHistoryTableMemberRow> getSessionHistoryTableMembers(Long sessionId) {
        return mapper.getSessionHistoryTableMembers(sessionId);
    }

    @Override
    public List<SessionHistoryParticipantRow> getSessionHistoryApplicants(Long sessionId) {
        return mapper.getSessionHistoryApplicants(sessionId);
    }

    @Override
    public SessionApplication findApplicationById(Long applicationId) {
        return mapper.findApplicationById(applicationId);
    }

    @Override
    public AttendanceSessionDetailRow getAttendanceSessionInfo(Long sessionId) {
        return mapper.getAttendanceSessionInfo(sessionId);
    }

}
