package com.buddy.attendance.service;

import com.buddy.attendance.dto.CheckInRequest;
import com.buddy.attendance.dto.CheckInResponse;
import com.buddy.attendance.dto.session.GetAttendanceSessionDetailResponse;
import com.buddy.attendance.dto.session.UpsertAttendanceCommand;
import com.buddy.attendance.dto.session.row.AttendanceSessionDetailRow;
import com.buddy.attendance.dto.session.sessionLists.AttendanceSessionItemResponse;
import com.buddy.attendance.dto.session.sessionLists.GetAttendanceSessionListResponse;
import com.buddy.attendance.model.Attendance;
import com.buddy.attendance.model.AttendanceSessionRow;
import com.buddy.attendance.model.AttendanceUserRow;
import com.buddy.attendance.model.AttendanceWord;
import com.buddy.attendance.model.enums.AttendanceMethod;
import com.buddy.attendance.model.enums.AttendanceStatus;
import com.buddy.attendance.repository.AttendanceRepository;
import com.buddy.session.model.Session;
import com.buddy.session.repository.SessionRepository;
import com.buddy.topic.dto.get.GetTopicQuestionResponse;
import com.buddy.topic.dto.get.GetTopicResponse;
import com.buddy.topic.dto.get.row.TopicDetailRow;
import com.buddy.topic.dto.get.row.TopicQuestionRow;
import com.buddy.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAttendanceServiceImpl implements UserAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final TopicRepository topicRepository;

    @Override
    public Attendance checkInQr(Long sessionId, Long userId) {
        // MVP: 서버에서 출석/지각 판정 로직은 나중에 고도화
        LocalDateTime now = LocalDateTime.now();

        Attendance att = new Attendance(
                1L // TODO:
                // QR 출첵 흐름 확정 전 임시 placeholder.
                // 현재는 upsert 호출 에러 회피를 위해 id를 고정값으로 넣음.
                // 정식 구현 시 DB 생성값 또는 (sessionId, userId) 기준 upsert 구조로 정리 필요.
                , sessionId
                , userId
                , AttendanceStatus.ATTENDED
                , now
                , AttendanceMethod.MANUAL
                , now
                , now
        );

        // attendanceRepository.upsertAttendance(att);
        return att;
    }

    @Override
    public List<Attendance> listBySession(Long sessionId) {
        return attendanceRepository.findBySession(sessionId);
    }

    @Override
    public GetAttendanceSessionListResponse getAttendanceSessions() {
        List<AttendanceSessionRow> rows = attendanceRepository.getAttendanceSessions();

        List<AttendanceSessionItemResponse> sessions = rows.stream()
                .map(row -> new AttendanceSessionItemResponse(
                        row.sessionId(),
                        row.title(),
                        row.startsAt(),
                        row.location()
                ))
                .toList();

        return new GetAttendanceSessionListResponse(sessions);
    }

    @Transactional
    @Override
    public GetAttendanceSessionDetailResponse getAttendanceSessionInfo(Long sessionId) {
        AttendanceSessionDetailRow row = sessionRepository.getAttendanceSessionInfo(sessionId);
        GetTopicResponse topicResponse = getAdminSessionTopic(sessionId);

        return new GetAttendanceSessionDetailResponse(
                row.sessionId(),
                row.title(),
                row.startsAt(),
                row.location(),
                topicResponse.mainTopic(),
                topicResponse.questions()
        );
    }

    @Override
    @Transactional
    public CheckInResponse checkIn(CheckInRequest req, Long sessionId) {
        AttendanceUserRow user = attendanceRepository.findUserByNameAndNickname(
                req.name(),
                req.nickname()
        );

        if (user == null) {
            throw new IllegalArgumentException("이름 또는 닉네임을 다시 확인해주세요.");
        }

        Integer applicationCount = attendanceRepository.countSessionApplicationBySessionIdAndUserId(
                sessionId,
                user.userId()
        );

        if (applicationCount == null || applicationCount == 0) {
            throw new IllegalArgumentException("해당 세션 참석자로 확인되지 않습니다.");
        }

        // 현재시간
        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = attendanceInsert(req, user, now, sessionId);

        Long attendanceWordId = insertAttendanceWord(req, user, now, sessionId);

        return new CheckInResponse(
                attendance.id(),
                attendanceWordId,
                sessionId,
                user.userId(),
                user.name(),
                user.nickname(),
                req.phrase(),
                attendance.status().name(),
                attendance.checkedInAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

    }

    private Long insertAttendanceWord(
            CheckInRequest req,
            AttendanceUserRow user,
            LocalDateTime now,
            Long sessionId
    ) {
        // 출석 영단어
        AttendanceWord attendanceWord = new AttendanceWord(
                null,
                sessionId,
                user.userId(),
                req.phrase(),
                req.example(),
                now,
                now
        );

        Long attendanceWordId =
                attendanceRepository.createAttendanceWord(attendanceWord);
        if (attendanceWordId == null) {
            throw new IllegalArgumentException("출석 저장에 실패했습니다.");
        }

        return attendanceWordId;
    }

    private Attendance attendanceInsert(
            CheckInRequest req,
            AttendanceUserRow user,
            LocalDateTime now,
            Long sessionId
    ) {

        Session session = sessionRepository.findSessionById(req.sessionId());
        if (session == null) {
            throw new IllegalArgumentException("세션 정보를 찾을 수 없습니다.");
        }

        // 출석 정보 디티오
        UpsertAttendanceCommand command = new UpsertAttendanceCommand(
                sessionId,
                user.userId(),
                resolveAttendanceStatus(session.getStartsAt(), now),
                now,
                AttendanceMethod.MANUAL,
                now,
                now
        );

        Long attendanceId = attendanceRepository.upsertAttendance(command);

        if (attendanceId == null) {
            throw new IllegalArgumentException("출석 저장에 실패했습니다.");
        }

        return new Attendance(
                attendanceId
                , sessionId
                , user.userId()
                , resolveAttendanceStatus(session.getStartsAt(), now)
                , now
                , AttendanceMethod.MANUAL
                , now
                , now
        );
    }

    private AttendanceStatus resolveAttendanceStatus(
            LocalDateTime startTime,
            LocalDateTime now
    ) {

        return now.isAfter(startTime)
                ? AttendanceStatus.LATE
                : AttendanceStatus.ATTENDED;
    }

    public GetTopicResponse getAdminSessionTopic(Long sessionId) {
        TopicDetailRow detailRow = topicRepository.getAdminSessionTopicDetail(sessionId);
        if (detailRow == null) {
            return new GetTopicResponse(
                    sessionId,
                    "",
                    "",
                    "",
                    "",
                    List.of()
            );
        }

        List<TopicQuestionRow> questionRows = topicRepository.getAdminSessionTopicQuestions(sessionId);

        return new GetTopicResponse(
                detailRow.sessionId(),
                detailRow.sessionTitle(),
                detailRow.startsAt(),
                detailRow.location(),
                detailRow.mainTopic(),
                getQuestionsResponse(questionRows)
        );
    }

    private List<GetTopicQuestionResponse> getQuestionsResponse(
            List<TopicQuestionRow> questionRows
    ) {
        List<GetTopicQuestionResponse> questions = new ArrayList<>();

        for (TopicQuestionRow row : questionRows) {
            questions.add(new GetTopicQuestionResponse(
                    row.questionId(),
                    row.orderNo(),
                    row.content()
            ));
        }

        return questions;
    }

}

