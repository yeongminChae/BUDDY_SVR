package com.buddy.session.service;

import com.buddy.session.model.Session;
import com.buddy.session.model.SessionApplication;

import java.util.List;

public interface UserSessionService {
    List<Session> listUpcomingSessions();

    void apply(Long sessionId, Long userId);

    void cancel(Long sessionId, Long userId);

    List<SessionApplication> listApplications(Long sessionId);
}
