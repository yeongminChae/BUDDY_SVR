package com.buddy.session.scheduler;

import com.buddy.session.scheduler.service.SessionStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionStatusScheduler {

    private final SessionStatusService service;

    @Scheduled(cron = "0 */5 * * * *")
    public void closedExpiredSessions() {
        log.info("세션 종료 스케쥴러 실행");

        int updatedCount = service.closeExpiredSessions();

        log.info("expired session close job done. updatedCount={}", updatedCount);
    }

}
