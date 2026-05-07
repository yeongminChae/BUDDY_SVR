package com.buddy.attendance.repository;

import com.buddy.attendance.dto.session.UpsertAttendanceCommand;
import com.buddy.attendance.mapper.AttendanceMapper;
import com.buddy.attendance.model.Attendance;
import com.buddy.attendance.model.AttendanceSessionRow;
import com.buddy.attendance.model.AttendanceUserRow;
import com.buddy.attendance.model.AttendanceWord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final AttendanceMapper mapper;

    @Override
    public Long upsertAttendance(UpsertAttendanceCommand attendance) {
        return mapper.upsert(attendance);
    }

    @Override
    public List<AttendanceSessionRow> getAttendanceSessions() {
        return mapper.getAttendanceSessions();
    }

    @Override
    public AttendanceUserRow findUserByNameAndNickname(String name, String nickname) {
        return mapper.findUserByNameAndNickname(name, nickname);
    }

    @Override
    public Integer countSessionApplicationBySessionIdAndUserId(Long sessionId, Long userId) {
        return mapper.countSessionApplicationBySessionIdAndUserId(sessionId, userId);
    }

    @Override
    public Long createAttendanceWord(AttendanceWord attendanceWord) {
        return mapper.createAttendanceWord(attendanceWord);
    }

    @Override
    public List<Attendance> findBySession(Long sessionId) {
        return mapper.findBySession(sessionId);
    }

    @Override
    public List<Long> findAbsentUserIdsBySessionId(Long sessionId) {
        return mapper.findAbsentUserIdsBySessionId(sessionId);
    }

}
