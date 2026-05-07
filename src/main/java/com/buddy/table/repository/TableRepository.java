package com.buddy.table.repository;

import com.buddy.table.dto.TableAssignmentRow;
import com.buddy.table.model.LevelGenderCountRow;
import com.buddy.table.model.RoundAssignmentRow;
import com.buddy.table.model.TableAssignment;
import com.buddy.table.model.TableAssignmentUpdateCommand;

import java.util.List;

public interface TableRepository {
    void assignTable(Long sessionId);

    List<TableAssignmentRow> findTableAssignmentRows(Long sessionId);

    TableAssignment findOne(Long sessionId, Long userId);

    Long getHostUserId(Long sessionId);

    int countConfirmedParticipants(Long sessionId);

    List<RoundAssignmentRow> findConfirmedParticipants(Long sessionId);

    int insertRoundAssignments(Long sessionId, List<TableAssignment> rows);

    List<LevelGenderCountRow> countByLevelAndGender(Long sessionId);

    int updateTableAssignments(Long sessionId, List<TableAssignmentUpdateCommand> command);

    int deleteAssignmentsBySession(Long sessionId);


}
