package com.buddy.topic.dto.get;

public record GetTopicQuestionResponse(
        Long questionId,
        Integer orderNo,
        String content
) {

}
