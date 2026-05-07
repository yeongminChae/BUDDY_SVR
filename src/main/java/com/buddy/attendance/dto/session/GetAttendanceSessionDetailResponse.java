package com.buddy.attendance.dto.session;

import com.buddy.topic.dto.get.GetTopicQuestionResponse;

import java.util.List;

public record GetAttendanceSessionDetailResponse(
        Long sessionId,
        String title,
        String startsAt,
        String location,
        String topicTitle,
        List<GetTopicQuestionResponse> topicQuestions
) {
}
