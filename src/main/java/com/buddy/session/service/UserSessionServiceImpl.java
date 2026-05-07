package com.buddy.session.service;

import com.buddy.session.model.Session;
import com.buddy.session.model.SessionApplication;
import com.buddy.session.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final SessionRepository sessionRepository;

    public UserSessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<Session> listUpcomingSessions() {
        return sessionRepository.findUpcoming();
    }

    @Override
    public void apply(Long sessionId, Long userId) {
        sessionRepository.upsertApplication(sessionId, userId, "APPLIED");
    }

    @Override
    public void cancel(Long sessionId, Long userId) {
        sessionRepository.upsertApplication(sessionId, userId, "CANCELED");
    }

    @Override
    public List<SessionApplication> listApplications(Long sessionId) {
        return sessionRepository.findApplications(sessionId);
    }
}
