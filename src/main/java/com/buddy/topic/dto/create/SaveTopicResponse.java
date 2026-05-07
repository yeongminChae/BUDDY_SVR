package com.buddy.topic.dto.create;

public record SaveTopicResponse(
        Long sessionId,
        String mainTopic,
        Integer questionCount
) {
}
