package com.buddy.user.repository;

import com.buddy.table.dto.update.UpdateTableAssignmentsRequest;
import com.buddy.user.dto.attendanceAward.AttendanceAwardCandidateRow;
import com.buddy.user.dto.attendanceAward.AttendanceAwardSummaryResponse;
import com.buddy.user.dto.patch.UpdateAdminUserStatusRequest;
import com.buddy.user.dto.userDetail.row.AdminUserDetailRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyAttendanceRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyWordRow;
import com.buddy.user.dto.userDetail.row.AdminUserRecentAttendedSessionRow;
import com.buddy.user.dto.userList.AdminUserListRow;
import com.buddy.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    List<AdminUserListRow> getAdminUserList(
            String query,
            Integer limit,
            Integer offset,
            String[] roles
    );

    int getTotalUserCount();

    int createUser(User user);

    List<AttendanceAwardCandidateRow> getAttendanceAwardCandidates(Integer year, Integer month);

    AttendanceAwardSummaryResponse getAttendanceSummary(Integer year, Integer month);

    Integer getUserAttendanceRate(Long userId);

    AdminUserDetailRow getAdminUserDetail(Long userId);

    AdminUserMonthlyAttendanceRow getAdminUserMonthlyAttendance(Long userId, Integer year, Integer month);

    AdminUserRecentAttendedSessionRow getAdminUserRecentAttendedSession(Long userId);

    List<AdminUserMonthlyWordRow> getAdminUserMonthlyWords(Long userId, Integer year, Integer month);

    Integer countUserBadges(Long userId);

    Integer updateUserRole(Long userId, String role, LocalDateTime now);

    Integer updateUserStatus(Long userId, String status, LocalDateTime now);

}
