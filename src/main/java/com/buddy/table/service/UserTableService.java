package com.buddy.table.service;

import com.buddy.table.model.TableAssignment;

public interface UserTableService {
    TableAssignment myAssignment(Long sessionId, Long userId);
}
