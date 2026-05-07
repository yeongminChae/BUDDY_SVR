package com.buddy.topic.dto.get.row;

public record TopicDetailRow(
        Long sessionId,
        String sessionTitle,
        String startsAt,
        String location,
        Long sessionTopicId,
        String mainTopic
) {
}
