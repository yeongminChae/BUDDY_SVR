package com.buddy.session.dto.admin.session_history;

import com.buddy.topic.dto.get.GetTopicQuestionResponse;

import java.util.List;

public record GetAdminSessionHistoryDetailResponse(
        Long sessionId,
        String title,
        String dateTime,
        String place,
        Integer attendanceRate,
        SessionHistoryAttendanceSummaryResponse attendanceSummary,
        List<SessionHistoryParticipantResponse> applicants,
        List<SessionHistoryParticipantResponse> participants,
        List<SessionHistoryTableRoundResponse> tableSummary,
        String topicTitle,
        List<GetTopicQuestionResponse> topicQuestions
) {
}
