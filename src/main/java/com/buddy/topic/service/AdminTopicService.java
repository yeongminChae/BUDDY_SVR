package com.buddy.topic.service;

import com.buddy.topic.dto.create.SaveTopicRequest;
import com.buddy.topic.dto.create.SaveTopicResponse;
import com.buddy.topic.dto.get.GetTopicResponse;

public interface AdminTopicService {
    SaveTopicResponse saveAdminSessionTopic(Long sessionId, SaveTopicRequest req);

    GetTopicResponse getAdminSessionTopic(Long sessionId);
}
