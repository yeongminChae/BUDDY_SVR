package com.buddy.user.mapper;

import com.buddy.user.dto.attendanceAward.AttendanceAwardCandidateRow;
import com.buddy.user.dto.attendanceAward.AttendanceAwardSummaryResponse;
import com.buddy.user.dto.userDetail.row.AdminUserDetailRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyAttendanceRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyWordRow;
import com.buddy.user.dto.userDetail.row.AdminUserRecentAttendedSessionRow;
import com.buddy.user.dto.userList.AdminUserListRow;
import com.buddy.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    User findById(@Param("id") Long id);

    List<AdminUserListRow> getAdminUserList(
            @Param("query") String query,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset,
            @Param("roles") List<String> roles
    );

    int getTotalUserCount();

    int createUser(User user);

    List<AttendanceAwardCandidateRow> getAdminAttendanceAward(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    AttendanceAwardSummaryResponse getAttendanceSummary(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    Integer getUserAttendanceRate(@Param("userId") Long userId);

    AdminUserDetailRow getAdminUserDetail(@Param("userId") Long userId);

    AdminUserMonthlyAttendanceRow getAdminUserMonthlyAttendance(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    AdminUserRecentAttendedSessionRow getAdminUserRecentAttendedSession(@Param("userId") Long userId);

    List<AdminUserMonthlyWordRow> getAdminUserMonthlyWords(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    Integer updateUserRole(
            @Param("userId") Long userId,
            @Param("role") String role,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    Integer updateUserStatus(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    // TODO : 뱃지기능 미구현
    // Integer countUserBadges(@Param("userId") Long userId);

}
