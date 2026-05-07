package com.buddy.topic.repository;

import com.buddy.topic.dto.create.SaveTopicItem;
import com.buddy.topic.dto.get.row.TopicDetailRow;
import com.buddy.topic.dto.get.row.TopicQuestionRow;
import com.buddy.topic.mapper.TopicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicRepository {

    private final TopicMapper mapper;

    @Override
    public Integer upsertSessionTopic(Long sessionId, String topic) {
        return mapper.upsertSessionTopic(sessionId, topic);
    }

    @Override
    public Long findSessionTopicIdBySessionId(Long sessionId) {
        return mapper.findSessionTopicIdBySessionId(sessionId);
    }

    @Override
    public Integer insertSessionTopicQuestions(Long sessionTopicId, List<SaveTopicItem> questions) {
        return mapper.insertSessionTopicQuestions(sessionTopicId, questions);
    }

    @Override
    public Integer deleteTopicQuestionsByTopicId(Long sessionTopicId) {
        return mapper.deleteTopicQuestionsByTopicId(sessionTopicId);
    }

    @Override
    public TopicDetailRow getAdminSessionTopicDetail(Long sessionId) {
        return mapper.getAdminSessionTopicDetail(sessionId);
    }

    @Override
    public List<TopicQuestionRow> getAdminSessionTopicQuestions(Long sessionId) {
        return mapper.getAdminSessionTopicQuestions(sessionId);
    }

}
