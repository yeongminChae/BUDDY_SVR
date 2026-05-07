package com.buddy.session.controller;

import com.buddy.common.web.ApiResponse;
import com.buddy.common.web.dto.ApplyRequest;
import com.buddy.session.model.Session;
import com.buddy.session.service.UserSessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/sessions")
public class UserSessionController {

    private final UserSessionService sessionService;

    public UserSessionController(UserSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ApiResponse<List<Session>> listUpcoming() {
        return ApiResponse.ok(sessionService.listUpcomingSessions());
    }

    @PostMapping("/{sessionId}/apply")
    public ApiResponse<Void> apply(@PathVariable Long sessionId, @RequestBody ApplyRequest req) {
        sessionService.apply(sessionId, req.getUserId());
        return ApiResponse.ok(null);
    }

    @PostMapping("/{sessionId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long sessionId, @RequestBody ApplyRequest req) {
        sessionService.cancel(sessionId, req.getUserId());
        return ApiResponse.ok(null);
    }
}
