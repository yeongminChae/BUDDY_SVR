package com.buddy.topic.controller;

import com.buddy.common.web.ApiResponse;
import com.buddy.topic.dto.create.SaveTopicRequest;
import com.buddy.topic.dto.create.SaveTopicResponse;
import com.buddy.topic.dto.get.GetTopicResponse;
import com.buddy.topic.service.AdminTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/sessions")
public class AdminTopicController {

    private final AdminTopicService service;

    @GetMapping("/{sessionId}/topic")
    public ResponseEntity<ApiResponse<GetTopicResponse>> getAdminSessionTopic(
            @PathVariable Long sessionId
    ) {
        GetTopicResponse response = service.getAdminSessionTopic(sessionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{sessionId}/topic")
    public ResponseEntity<ApiResponse<SaveTopicResponse>> saveAdminSessionTopic(
            @PathVariable Long sessionId,
            @RequestBody SaveTopicRequest req
    ) {

        SaveTopicResponse response = service.saveAdminSessionTopic(sessionId, req);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
