package com.buddy.session.mapper;

import com.buddy.attendance.dto.session.row.AttendanceSessionDetailRow;
import com.buddy.session.dto.admin.session_applicant.get.AdminParticipantItemResponse;
import com.buddy.session.dto.admin.session_candidate.get.AdminParticipantAddCandidateItemResponse;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryDetailRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryParticipantRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryTableMemberRow;
import com.buddy.session.model.Session;
import com.buddy.session.model.SessionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SessionMapper {
    List<Session> findUpcoming();

    List<Session> findPast();

    void upsertApplication(@Param("sessionId") Long sessionId,
                           @Param("userId") Long userId,
                           @Param("status") String status);

    Session findSessionById(@Param("sessionId") Long sessionId);

    int countTableAssignmentsBySessionId(@Param("sessionId") Long sessionId);

    List<SessionApplication> findApplications(@Param("sessionId") Long sessionId);

    Integer insertSession(Session session);

    List<AdminParticipantItemResponse> findSessionParticipants(@Param("sessionId") Long sessionId);

    int cancelSessionApplication(
            @Param("applicationId") Long applicationId,
            @Param("sessionId") Long sessionId,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    List<AdminParticipantAddCandidateItemResponse> getParticipantCandidates(
            @Param("sessionId") Long sessionId,
            @Param("query") String query,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset
    );

    int addSessionParticipants(
            @Param("sessionId") Long sessionId,
            @Param("userIds") List<Long> userIds,
            @Param("status") String status,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    int closeExpiredSessions(@Param("now") String now);

    int closeSessions(@Param("sessionId") Long sessionId);

    List<Long> findClosableSessionIds(@Param("now") LocalDateTime now);

    SessionHistoryDetailRow getSessionHistoryDetail(@Param("sessionId") Long sessionId);

    List<SessionHistoryParticipantRow> getSessionHistoryParticipants(@Param("sessionId") Long sessionId);

    List<SessionHistoryTableMemberRow> getSessionHistoryTableMembers(@Param("sessionId") Long sessionId);

    List<SessionHistoryParticipantRow> getSessionHistoryApplicants(@Param("sessionId") Long sessionId);

    SessionApplication findApplicationById(@Param("applicationId") Long applicationId);

    AttendanceSessionDetailRow getAttendanceSessionInfo(@Param("sessionId") Long sessionId);
}
