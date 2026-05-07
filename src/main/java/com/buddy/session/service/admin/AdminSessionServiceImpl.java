package com.buddy.session.service.admin;

import com.buddy.attendance.dto.session.UpsertAttendanceCommand;
import com.buddy.attendance.model.enums.AttendanceMethod;
import com.buddy.attendance.model.enums.AttendanceStatus;
import com.buddy.attendance.repository.AttendanceRepository;
import com.buddy.session.dto.admin.session.create.CreateSessionRequest;
import com.buddy.session.dto.admin.session.create.CreateSessionResponse;
import com.buddy.session.dto.admin.session.get.AdminSessionListItemResponse;
import com.buddy.session.dto.admin.session.get.GetAdminSessionListResponse;
import com.buddy.session.dto.admin.session.get.GetAdminSessionManageResponse;
import com.buddy.session.dto.admin.session_applicant.get.AdminParticipantItemResponse;
import com.buddy.session.dto.admin.session_applicant.get.GetAdminParticipantManageResponse;
import com.buddy.session.dto.admin.session_candidate.create.AddSessionParticipantsRequest;
import com.buddy.session.dto.admin.session_candidate.create.AddSessionParticipantsResponse;
import com.buddy.session.dto.admin.session_candidate.get.AdminParticipantAddCandidateItemResponse;
import com.buddy.session.dto.admin.session_candidate.get.GetAdminParticipantAddCandidateResponse;
import com.buddy.session.model.Session;
import com.buddy.session.model.SessionApplication;
import com.buddy.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSessionServiceImpl implements AdminSessionService {

    private final SessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public GetAdminParticipantManageResponse getAdminSessionParticipants(Long sessionId) {
        Session session = sessionRepository.findSessionById(sessionId);
        if (session == null) throw new IllegalArgumentException("존재하지 않는 세션입니다.");

        List<AdminParticipantItemResponse> sessionParticipants =
                sessionRepository.findSessionParticipants(sessionId);
        if (sessionParticipants == null) throw new IllegalArgumentException("정보가 존재하지 않습니다.");

        return new GetAdminParticipantManageResponse(
                sessionId,
                session.getTitle(),
                session.getAttendanceCount(),
                session.getCapacity(),
                sessionParticipants
        );
    }

    @Override
    public GetAdminSessionListResponse getAdminSessionList() {
        List<Session> upcomingSessions = sessionRepository.findUpcoming();
        List<Session> pastSessions = sessionRepository.findPast();

        List<AdminSessionListItemResponse> upcoming = upcomingSessions.stream()
                .map(session -> getAdminSessionListItemResponse(session))
                .toList();

        List<AdminSessionListItemResponse> past = pastSessions.stream()
                .map(session -> getAdminSessionListItemResponse(session))
                .toList();

        return new GetAdminSessionListResponse(upcoming, past);
    }

    @Override
    public GetAdminSessionManageResponse getAdminSessionManage(Long sessionId) {
        Session session = sessionRepository.findSessionById(sessionId);

        if (session == null) {
            throw new IllegalArgumentException("해당 세션이 없습니다. sessionId=" + sessionId);
        }

        boolean hasTableAssignments =
                sessionRepository.countTableAssignmentsBySessionId(sessionId) > 0;

        String startsAtText = session.getStartsAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new GetAdminSessionManageResponse(
                session.getId(),
                session.getTitle(),
                startsAtText,
                session.getLocation(),
                session.getAttendanceCount(),
                session.getCapacity(),
                hasTableAssignments,
                session.getStatus())
                ;
    }

    @Override
    public GetAdminParticipantAddCandidateResponse getParticipantCandidates(
            Long sessionId, String query, Integer limit, Integer offset
    ) {
        List<AdminParticipantAddCandidateItemResponse> participantCandidates =
                sessionRepository.getParticipantCandidates(sessionId, query, limit + 1, offset);
        if (participantCandidates == null) throw new IllegalArgumentException("조회가 잘못 되었습니다.");

        boolean hasNext = participantCandidates.size() > limit;
        List<AdminParticipantAddCandidateItemResponse> result =
                participantCandidates.subList(0, Math.min(limit, participantCandidates.size()));

        return new GetAdminParticipantAddCandidateResponse(
                sessionId,
                query,
                limit,
                hasNext,
                result
        );
    }

    @Override
    public AddSessionParticipantsResponse addSessionParticipants(
            Long sessionId,
            AddSessionParticipantsRequest request
    ) {

        String status = "CONFIRMED";
        LocalDateTime now = LocalDateTime.now();
        List<Long> userIds = request.userIds();

        int result = sessionRepository.insertSessionParticipants(
                sessionId,
                userIds,
                status,
                now,
                now
        );
        if (result != userIds.size()) throw new IllegalStateException("저장 도중 에러 발생");

        return new AddSessionParticipantsResponse(sessionId, result);
    }

    @Override
    public CreateSessionResponse createSession(CreateSessionRequest req) {

        LocalDateTime startsAt = LocalDateTime.of(req.sessionDate(), req.sessionTime());
        LocalDateTime now = LocalDateTime.now();

        if (startsAt.isBefore(now)) {
            throw new IllegalArgumentException("현재 시간 이전의 세션은 생성할 수 없습니다.");
        }

        Session session = getSession(req, startsAt, now);

        Integer result = sessionRepository.insertSession(session);
        if (result != 1) {
            throw new IllegalStateException("데이터 저장 도중 에러 발생");
        }

        sessionRepository.upsertApplication(
                session.getId(), session.getHostId(), "CONFIRMED"
        );
        Session insertedSession = sessionRepository.findSessionById(session.getId());

        return getCreateSessionResponse(insertedSession);
    }

    private static CreateSessionResponse getCreateSessionResponse(Session insertedSession) {
        String startsAtText = insertedSession.getStartsAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new CreateSessionResponse(
                insertedSession.getId(),
                insertedSession.getTitle(),
                startsAtText,
                insertedSession.getLocation(),
                insertedSession.getCapacity(),
                insertedSession.getTopic(),
                insertedSession.getStatus()
        );
    }

    private static Session getSession(
            CreateSessionRequest req,
            LocalDateTime startsAtText,
            LocalDateTime nowText
    ) {
        Session session = new Session();

        session.setStartsAt(startsAtText);
        session.setLocation(req.location());
        session.setTopic(req.topic());
        session.setType("REGULAR");
        session.setCapacity(req.capacity());
        session.setCreatedAt(nowText);
        session.setUpdatedAt(nowText);
        session.setTitle(req.title());
        session.setHostId(req.hostUserId());

        return session;
    }

    private static AdminSessionListItemResponse getAdminSessionListItemResponse(Session session) {
        String startsAtText = session.getStartsAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new AdminSessionListItemResponse(
                session.getId(),
                session.getTitle(),
                startsAtText,
                session.getLocation(),
                session.getAttendanceCount(),
                session.getCapacity(),
                session.getStatus()
        );
    }

    @Override
    @Transactional
    public int closeSession(Long sessionId) {
        return closeSessionAndMarkAbsences(sessionId);
    }

    @Override
    public void cancelSessionApplication(Long sessionId, Long applicationId) {

        Session session = sessionRepository.findSessionById(sessionId);

        if (session == null) {
            throw new IllegalArgumentException("세션 아이디가 잘못되었습니다.");
        }

        SessionApplication application = sessionRepository.findApplicationById(applicationId);

        if (application == null) {
            throw new IllegalArgumentException("신청 정보가 없습니다.");
        }

        if (application.sessionId().equals(sessionId) == false) {
            throw new IllegalArgumentException("세션 참가자 정보가 올바르지 않습니다.");
        }

        if (session.getHostId().equals(application.userId())) {
            throw new IllegalArgumentException("세션 담당 리더는 제거할 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        int result = sessionRepository.cancelSessionApplication(
                applicationId,
                sessionId,
                now);
        if (result != 1) throw new IllegalArgumentException("삭제에 실패 하였습니다.");
    }

    private int closeSessionAndMarkAbsences(Long sessionId) {
        int closeResult = sessionRepository.closeSession(sessionId);
        if (closeResult < 1) {
            throw new IllegalArgumentException("세션 종료 실패");
        }

        List<Long> absentUserIds =
                attendanceRepository.findAbsentUserIdsBySessionId(sessionId);

        LocalDateTime now = LocalDateTime.now();
        int successCount = 0;
        for (Long userId : absentUserIds) {
            UpsertAttendanceCommand command = new UpsertAttendanceCommand(
                    sessionId,
                    userId,
                    AttendanceStatus.ABSENT,
                    null,
                    AttendanceMethod.MANUAL,
                    now,
                    now
            );

            Long attendanceId = attendanceRepository.upsertAttendance(command);
            if (attendanceId == null) {
                throw new IllegalArgumentException("결석 삽입 실패");
            }

            successCount++;
        }

        if (successCount != absentUserIds.size()) {
            throw new IllegalArgumentException("결석 삽입 이상");
        }

        return 1;
    }

}
