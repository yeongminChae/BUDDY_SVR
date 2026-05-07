package com.buddy.table.service;

import com.buddy.table.model.TableAssignment;
import com.buddy.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTableServiceImpl implements UserTableService {

    private TableRepository tableRepository;

    @Override
    public TableAssignment myAssignment(Long sessionId, Long userId) {
        return tableRepository.findOne(sessionId, userId);
    }

}
