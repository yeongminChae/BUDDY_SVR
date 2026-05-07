package com.buddy.attendance.repository;

import com.buddy.attendance.dto.session.UpsertAttendanceCommand;
import com.buddy.attendance.model.Attendance;
import com.buddy.attendance.model.AttendanceSessionRow;
import com.buddy.attendance.model.AttendanceUserRow;
import com.buddy.attendance.model.AttendanceWord;

import java.util.List;

public interface AttendanceRepository {
    Long upsertAttendance(UpsertAttendanceCommand attendance);

    List<AttendanceSessionRow> getAttendanceSessions();

    AttendanceUserRow findUserByNameAndNickname(String name, String nickname);

    Integer countSessionApplicationBySessionIdAndUserId(Long sessionId, Long userId);

    Long createAttendanceWord(AttendanceWord attendanceWord);

    // long getLastInsertedAttendanceWordId();

    List<Attendance> findBySession(Long sessionId);

    List<Long> findAbsentUserIdsBySessionId(Long sessionId);

}
