package com.buddy.user.repository;

import com.buddy.user.dto.attendanceAward.AttendanceAwardCandidateRow;
import com.buddy.user.dto.attendanceAward.AttendanceAwardSummaryResponse;
import com.buddy.user.dto.userDetail.row.AdminUserDetailRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyAttendanceRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyWordRow;
import com.buddy.user.dto.userDetail.row.AdminUserRecentAttendedSessionRow;
import com.buddy.user.dto.userList.AdminUserListRow;
import com.buddy.user.mapper.UserMapper;
import com.buddy.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<AdminUserListRow> getAdminUserList(
            String query,
            Integer limit,
            Integer offset,
            String[] roles
    ) {
        List<String> rolesList = roles != null && roles.length > 0
                ? Arrays.stream(roles).toList()
                : new ArrayList<>();

        return mapper.getAdminUserList(query, limit, offset, rolesList);
    }

    @Override
    public int getTotalUserCount() {
        return mapper.getTotalUserCount();
    }

    @Override
    public int createUser(User user) {
        return mapper.createUser(user);
    }

    @Override
    public List<AttendanceAwardCandidateRow> getAttendanceAwardCandidates(Integer year, Integer month) {
        return mapper.getAdminAttendanceAward(year, month);
    }

    @Override
    public AttendanceAwardSummaryResponse getAttendanceSummary(Integer year, Integer month) {
        return mapper.getAttendanceSummary(year, month);
    }

    @Override
    public Integer getUserAttendanceRate(Long userId) {
        return mapper.getUserAttendanceRate(userId);
    }

    @Override
    public AdminUserDetailRow getAdminUserDetail(Long userId) {
        return mapper.getAdminUserDetail(userId);
    }

    @Override
    public AdminUserMonthlyAttendanceRow getAdminUserMonthlyAttendance(
            Long userId,
            Integer year,
            Integer month
    ) {
        return mapper.getAdminUserMonthlyAttendance(userId, year, month);
    }

    @Override
    public AdminUserRecentAttendedSessionRow getAdminUserRecentAttendedSession(Long userId) {
        return mapper.getAdminUserRecentAttendedSession(userId);
    }

    @Override
    public List<AdminUserMonthlyWordRow> getAdminUserMonthlyWords(
            Long userId,
            Integer year,
            Integer month
    ) {
        return mapper.getAdminUserMonthlyWords(userId, year, month);
    }

    @Override
    public Integer countUserBadges(Long userId) {
        return 0;
    }

    @Override
    public Integer updateUserRole(Long userId, String role, LocalDateTime now) {
        return mapper.updateUserRole(userId, role, now);
    }

    @Override
    public Integer updateUserStatus(Long userId, String status, LocalDateTime now) {
        return mapper.updateUserStatus(userId, status, now);
    }


}
