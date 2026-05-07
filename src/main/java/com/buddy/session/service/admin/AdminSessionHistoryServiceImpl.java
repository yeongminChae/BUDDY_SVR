package com.buddy.session.service.admin;

import com.buddy.session.dto.admin.session_history.*;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryDetailRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryParticipantRow;
import com.buddy.session.dto.admin.session_history.row.SessionHistoryTableMemberRow;
import com.buddy.session.repository.SessionRepository;
import com.buddy.topic.dto.get.GetTopicQuestionResponse;
import com.buddy.topic.dto.get.GetTopicResponse;
import com.buddy.topic.dto.get.row.TopicDetailRow;
import com.buddy.topic.dto.get.row.TopicQuestionRow;
import com.buddy.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSessionHistoryServiceImpl implements AdminSessionHistoryService {

    private final SessionRepository sessionRepository;
    private final TopicRepository topicRepository;

    @Transactional
    @Override
    public GetAdminSessionHistoryDetailResponse getAdminSessionHistoryDetail(Long sessionId) {
        SessionHistoryDetailRow sessionHistoryDetail =
                sessionRepository.getSessionHistoryDetail(sessionId);

        if (sessionHistoryDetail == null) {
            throw new IllegalArgumentException("종료 세션 기록이 없습니다. sessionId=" + sessionId);
        }

        List<SessionHistoryParticipantRow> applicantRows =
                sessionRepository.getSessionHistoryApplicants(sessionId);

        List<SessionHistoryParticipantRow> participantRows =
                sessionRepository.getSessionHistoryParticipants(sessionId);

        List<SessionHistoryTableMemberRow> tableMemberRows =
                sessionRepository.getSessionHistoryTableMembers(sessionId);

        GetTopicResponse adminSessionTopic = getAdminSessionTopic(sessionId);

        return new GetAdminSessionHistoryDetailResponse(
                sessionHistoryDetail.sessionId(),
                sessionHistoryDetail.title(),
                sessionHistoryDetail.startsAt(),
                sessionHistoryDetail.location(),
                sessionHistoryDetail.attendanceRate(),
                getSessionHistoryAttendanceSummary(sessionHistoryDetail),
                getSessionHistoryParticipant(applicantRows),
                getSessionHistoryParticipant(participantRows),
                getSessionHistoryTableRound(tableMemberRows),
                adminSessionTopic.mainTopic(),
                adminSessionTopic.questions()
        );

    }

    private List<SessionHistoryTableRoundResponse> getSessionHistoryTableRound(
            List<SessionHistoryTableMemberRow> sessionHistoryTableMembers
    ) {
        List<SessionHistoryTableRoundResponse> tableResponse = new ArrayList<>();
        tableResponse.add(new SessionHistoryTableRoundResponse(
                1,
                getSessionHistoryTable(sessionHistoryTableMembers, 1)
        ));
        tableResponse.add(new SessionHistoryTableRoundResponse(
                2,
                getSessionHistoryTable(sessionHistoryTableMembers, 2)
        ));

        return tableResponse;
    }

    private List<SessionHistoryTableResponse> getSessionHistoryTable(
            List<SessionHistoryTableMemberRow> sessionHistoryTableMembers,
            int roundNo
    ) {
        Map<Integer, List<SessionHistoryTableMemberRow>> map =
                getSessionHistoryMap(sessionHistoryTableMembers, roundNo);

        return map.entrySet().stream()
                .map(entry -> new SessionHistoryTableResponse(
                        entry.getKey() + "번 테이블",
                        entry.getValue().stream()
                                .map(SessionHistoryTableMemberRow::name)
                                .toList()
                ))
                .toList();
    }

    private Map<Integer, List<SessionHistoryTableMemberRow>> getSessionHistoryMap(
            List<SessionHistoryTableMemberRow> sessionHistoryTableMembers,
            int roundNo
    ) {
        return sessionHistoryTableMembers.stream()
                .filter(i -> i.roundNo() == roundNo)
                .collect(Collectors.groupingBy(
                        SessionHistoryTableMemberRow::tableNo,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

    }

    private List<SessionHistoryParticipantResponse> getSessionHistoryParticipant(
            List<SessionHistoryParticipantRow> sessionHistoryParticipants
    ) {
        return sessionHistoryParticipants.stream()
                .map(row -> new SessionHistoryParticipantResponse(
                        row.userId(),
                        row.name(),
                        row.nickname()
                ))
                .toList();

    }

    private SessionHistoryAttendanceSummaryResponse getSessionHistoryAttendanceSummary(
            SessionHistoryDetailRow sessionHistoryDetail
    ) {
        return new SessionHistoryAttendanceSummaryResponse(
                sessionHistoryDetail.attendedCount(),
                sessionHistoryDetail.appliedCount(),
                sessionHistoryDetail.noShowCount()
        );
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
