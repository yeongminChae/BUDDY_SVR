package com.buddy.session.service.admin;

import com.buddy.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminSessionApplicationServiceImpl implements AdminSessionApplicationService {

    private final SessionRepository repository;

    @Override
    public void cancelSessionApplication(Long applicationId) {
        LocalDateTime now = LocalDateTime.now();

        int result = repository.cancelSessionApplication(applicationId, 1L, now);
        if (result != 1) throw new IllegalArgumentException("삭제에 실패 하였습니다.");

    }

}
