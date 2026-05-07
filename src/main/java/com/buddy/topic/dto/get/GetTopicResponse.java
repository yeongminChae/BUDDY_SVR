package com.buddy.topic.dto.get;

import java.util.List;

public record GetTopicResponse(
        Long sessionId,
        String sessionTitle,
        String startsAt,
        String location,
        String mainTopic,
        List<GetTopicQuestionResponse> questions
) {
}
