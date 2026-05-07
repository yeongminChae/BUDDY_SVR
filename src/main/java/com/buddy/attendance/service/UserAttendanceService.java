package com.buddy.attendance.service;

import com.buddy.attendance.dto.CheckInRequest;
import com.buddy.attendance.dto.CheckInResponse;
import com.buddy.attendance.dto.session.GetAttendanceSessionDetailResponse;
import com.buddy.attendance.dto.session.sessionLists.GetAttendanceSessionListResponse;
import com.buddy.attendance.model.Attendance;

import java.util.List;

public interface UserAttendanceService {
    Attendance checkInQr(Long sessionId, Long userId);

    GetAttendanceSessionListResponse getAttendanceSessions();

    GetAttendanceSessionDetailResponse getAttendanceSessionInfo(Long sessionId);

    CheckInResponse checkIn(CheckInRequest req, Long sessionId);

    List<Attendance> listBySession(Long sessionId);

}
