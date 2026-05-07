package com.buddy.session.scheduler.service;

import com.buddy.session.repository.SessionRepository;
import com.buddy.session.service.admin.AdminSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionStatusServiceImpl implements SessionStatusService {

    private final SessionRepository repository;
    private final AdminSessionService service;

    @Override
    public int closeExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();

        List<Long> sessionIds = repository.findClosableSessionIds(now);

        int updatedCount = 0;

        for (Long sessionId : sessionIds) {
            updatedCount += service.closeSession(sessionId);
        }

        return updatedCount;
    }

}