package com.buddy.attendance.controller;

import com.buddy.attendance.dto.CheckInRequest;
import com.buddy.attendance.dto.CheckInResponse;
import com.buddy.attendance.dto.session.GetAttendanceSessionDetailResponse;
import com.buddy.attendance.dto.session.sessionLists.GetAttendanceSessionListResponse;
import com.buddy.attendance.model.Attendance;
import com.buddy.attendance.service.UserAttendanceService;
import com.buddy.common.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/user/attendance")
@RequestMapping("/api/public") // TODO : 추후 고도화 시 user로 uri 변경 예정
public class UserAttendanceController {

    private final UserAttendanceService attendanceService;

    // MVP: QR 체크인은 sessionId/userId만 받는 단순 형태
    @PostMapping("/qr")
    public ApiResponse<Attendance> checkIn(@RequestParam Long sessionId, @RequestParam Long userId) {
        return ApiResponse.ok(attendanceService.checkInQr(sessionId, userId));
    }

    @GetMapping("/attendance/sessions")
    public ResponseEntity<ApiResponse<GetAttendanceSessionListResponse>> getAttendanceSessions() {
        GetAttendanceSessionListResponse response = attendanceService.getAttendanceSessions();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sessions/{sessionId}/attendance")
    public ResponseEntity<ApiResponse<GetAttendanceSessionDetailResponse>> getAttendanceSessionInfo(
            @PathVariable Long sessionId
    ) {
        GetAttendanceSessionDetailResponse response =
                attendanceService.getAttendanceSessionInfo(sessionId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/sessions/{sessionId}/attendance-words")
    public ResponseEntity<ApiResponse<CheckInResponse>> createAttendanceWord(
            @Valid @RequestBody CheckInRequest req,
            @PathVariable Long sessionId
    ) {
        CheckInResponse response = attendanceService.checkIn(req, sessionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
