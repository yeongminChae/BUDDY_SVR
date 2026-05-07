package com.buddy.topic.repository;

import com.buddy.topic.dto.create.SaveTopicItem;
import com.buddy.topic.dto.get.row.TopicDetailRow;
import com.buddy.topic.dto.get.row.TopicQuestionRow;

import java.util.List;

public interface TopicRepository {
    Integer upsertSessionTopic(Long sessionId, String topic);

    Long findSessionTopicIdBySessionId(Long sessionId);

    Integer insertSessionTopicQuestions(
            Long sessionTopicId,
            List<SaveTopicItem> questions
    );

    Integer deleteTopicQuestionsByTopicId(Long sessionTopicId);

    TopicDetailRow getAdminSessionTopicDetail(Long sessionId);
    
    List<TopicQuestionRow> getAdminSessionTopicQuestions(Long sessionId);

}
