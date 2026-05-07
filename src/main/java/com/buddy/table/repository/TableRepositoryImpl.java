package com.buddy.table.repository;

import com.buddy.table.dto.TableAssignmentRow;
import com.buddy.table.mapper.TableMapper;
import com.buddy.table.model.LevelGenderCountRow;
import com.buddy.table.model.RoundAssignmentRow;
import com.buddy.table.model.TableAssignment;
import com.buddy.table.model.TableAssignmentUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TableRepositoryImpl implements TableRepository {

    private final TableMapper mapper;

    @Override
    public void assignTable(Long sessionId) {
        mapper.assignTable(sessionId);
    }

    @Override
    public List<TableAssignmentRow> findTableAssignmentRows(Long sessionId) {
        return mapper.findBySession(sessionId);
    }

    @Override
    public TableAssignment findOne(Long sessionId, Long userId) {
        return mapper.findOne(sessionId, userId);
    }

    @Override
    public Long getHostUserId(Long sessionId) {
        return mapper.getHostUserId(sessionId);
    }

    @Override
    public int countConfirmedParticipants(Long sessionId) {
        return mapper.countConfirmedParticipants(sessionId);
    }

    @Override
    public List<RoundAssignmentRow> findConfirmedParticipants(Long sessionId) {
        return mapper.findConfirmedParticipants(sessionId);
    }

    @Override
    public int insertRoundAssignments(Long sessionId, List<TableAssignment> rows) {
        return rows.isEmpty() ? 0 : mapper.insertRoundAssignments(rows);
    }

    @Override
    public List<LevelGenderCountRow> countByLevelAndGender(Long sessionId) {
        return mapper.countByLevelAndGender(sessionId);
    }

    @Override
    public int updateTableAssignments(Long sessionId, List<TableAssignmentUpdateCommand> changes) {
        return mapper.updateTableAssignments(sessionId, changes);
    }

    @Override
    public int deleteAssignmentsBySession(Long sessionId) {
        return mapper.deleteAssignmentsBySession(sessionId);
    }

}
