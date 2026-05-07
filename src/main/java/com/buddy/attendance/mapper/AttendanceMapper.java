package com.buddy.attendance.mapper;

import com.buddy.attendance.dto.session.UpsertAttendanceCommand;
import com.buddy.attendance.model.Attendance;
import com.buddy.attendance.model.AttendanceSessionRow;
import com.buddy.attendance.model.AttendanceUserRow;
import com.buddy.attendance.model.AttendanceWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceMapper {

    Long upsert(UpsertAttendanceCommand attendance);

    List<AttendanceSessionRow> getAttendanceSessions();

    AttendanceUserRow findUserByNameAndNickname(
            @Param("name") String name,
            @Param("nickname") String nickname
    );

    Integer countSessionApplicationBySessionIdAndUserId(
            @Param("sessionId") Long sessionId,
            @Param("userId") Long userId
    );

    Long createAttendanceWord(AttendanceWord attendanceWord);

    List<Attendance> findBySession(@Param("sessionId") Long sessionId);

    List<Long> findAbsentUserIdsBySessionId(@Param("sessionId") Long sessionId);

}
