package com.buddy.topic.service;


import com.buddy.topic.dto.create.SaveTopicRequest;
import com.buddy.topic.dto.create.SaveTopicResponse;
import com.buddy.topic.dto.get.GetTopicQuestionResponse;
import com.buddy.topic.dto.get.GetTopicResponse;
import com.buddy.topic.dto.get.row.TopicDetailRow;
import com.buddy.topic.dto.get.row.TopicQuestionRow;
import com.buddy.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTopicServiceImpl implements AdminTopicService {

    private final TopicRepository repository;

    @Transactional
    @Override
    public SaveTopicResponse saveAdminSessionTopic(Long sessionId, SaveTopicRequest req) {
        Integer upsertTopicResult = repository.upsertSessionTopic(sessionId, req.mainTopic());
        if (upsertTopicResult == null || upsertTopicResult < 1)
            throw new IllegalArgumentException("주제 저장 실패");

        Long topicId = repository.findSessionTopicIdBySessionId(sessionId);
        if (topicId == null) throw new IllegalArgumentException("주제 조회 실패");

        repository.deleteTopicQuestionsByTopicId(topicId);

        Integer result = 0;
        if (req.questions().isEmpty() == false) {
            result = repository.insertSessionTopicQuestions(topicId, req.questions());

            if (result != req.questions().size()) throw new IllegalArgumentException("질문 저장 실패");
        }

        return new SaveTopicResponse(
                sessionId,
                req.mainTopic(),
                req.questions().size()
        );

    }

    @Override
    public GetTopicResponse getAdminSessionTopic(Long sessionId) {
        TopicDetailRow detailRow = repository.getAdminSessionTopicDetail(sessionId);
        if (detailRow == null) {
            return new GetTopicResponse(
                    sessionId,
                    "",
                    "",
                    "",
                    "",
                    List.of()
            );
        }

        List<TopicQuestionRow> questionRows = repository.getAdminSessionTopicQuestions(sessionId);

        return new GetTopicResponse(
                detailRow.sessionId(),
                detailRow.sessionTitle(),
                detailRow.startsAt(),
                detailRow.location(),
                detailRow.mainTopic(),
                getQuestionsResponse(questionRows)
        );
    }

    private List<GetTopicQuestionResponse> getQuestionsResponse(
            List<TopicQuestionRow> questionRows
    ) {
        List<GetTopicQuestionResponse> questions = new ArrayList<>();

        for (TopicQuestionRow row : questionRows) {
            questions.add(new GetTopicQuestionResponse(
                    row.questionId(),
                    row.orderNo(),
                    row.content()
            ));
        }

        return questions;
    }

}
