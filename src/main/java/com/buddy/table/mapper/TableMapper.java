package com.buddy.table.mapper;

import com.buddy.table.dto.TableAssignmentRow;
import com.buddy.table.model.LevelGenderCountRow;
import com.buddy.table.model.RoundAssignmentRow;
import com.buddy.table.model.TableAssignment;
import com.buddy.table.model.TableAssignmentUpdateCommand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TableMapper {
    void assignTable(@Param("sessionId") Long sessionId);

    List<TableAssignmentRow> findBySession(@Param("sessionId") Long sessionId);

    TableAssignment findOne(
            @Param("sessionId") Long sessionId,
            @Param("userId") Long userId
    );

    Long getHostUserId(@Param("sessionId") Long sessionId);

    int countConfirmedParticipants(@Param("sessionId") Long sessionId);

    int insertRoundAssignments(@Param("rows") List<TableAssignment> rows);

    List<RoundAssignmentRow> findConfirmedParticipants(@Param("sessionId") Long sessionId);

    List<LevelGenderCountRow> countByLevelAndGender(@Param("sessionId") Long sessionId);

    int updateTableAssignments(@Param("sessionId") Long sessionId,
                               @Param("changes") List<TableAssignmentUpdateCommand> changes);

    int deleteAssignmentsBySession(@Param("sessionId") Long sessionId);
}
