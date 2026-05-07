package com.buddy.topic.mapper;

import com.buddy.topic.dto.create.SaveTopicItem;
import com.buddy.topic.dto.get.row.TopicDetailRow;
import com.buddy.topic.dto.get.row.TopicQuestionRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TopicMapper {
    Integer upsertSessionTopic(
            @Param("sessionId") Long sessionId,
            @Param("topic") String topic
    );

    Long findSessionTopicIdBySessionId(@Param("sessionId") Long sessionId);

    Integer insertSessionTopicQuestions(
            @Param("sessionTopicId") Long sessionTopicId,
            @Param("questions") List<SaveTopicItem> questions
    );

    int deleteTopicQuestionsByTopicId(@Param("sessionTopicId") Long sessionTopicId);

    TopicDetailRow getAdminSessionTopicDetail(@Param("sessionId") Long sessionId);

    List<TopicQuestionRow> getAdminSessionTopicQuestions(@Param("sessionId") Long sessionId);

}
