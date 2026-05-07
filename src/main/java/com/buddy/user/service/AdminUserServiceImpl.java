package com.buddy.user.service;

import com.buddy.user.dto.attendanceAward.AttendanceAwardCandidateResponse;
import com.buddy.user.dto.attendanceAward.AttendanceAwardCandidateRow;
import com.buddy.user.dto.attendanceAward.GetAdminAttendanceAwardResponse;
import com.buddy.user.dto.create.CreateAdminUserRequest;
import com.buddy.user.dto.create.CreateAdminUserResponse;
import com.buddy.user.dto.patch.UpdateAdminUserRoleRequest;
import com.buddy.user.dto.patch.UpdateAdminUserRoleResponse;
import com.buddy.user.dto.patch.UpdateAdminUserStatusRequest;
import com.buddy.user.dto.patch.UpdateAdminUserStatusResponse;
import com.buddy.user.dto.userDetail.AdminUserMonthlyAttendanceResponse;
import com.buddy.user.dto.userDetail.AdminUserMonthlyWordResponse;
import com.buddy.user.dto.userDetail.AdminUserRecentAttendedSessionResponse;
import com.buddy.user.dto.userDetail.GetAdminUserDetailResponse;
import com.buddy.user.dto.userDetail.row.AdminUserDetailRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyAttendanceRow;
import com.buddy.user.dto.userDetail.row.AdminUserMonthlyWordRow;
import com.buddy.user.dto.userDetail.row.AdminUserRecentAttendedSessionRow;
import com.buddy.user.dto.userList.AdminUserListItemResponse;
import com.buddy.user.dto.userList.AdminUserListRow;
import com.buddy.user.dto.userList.GetAdminUserListResponse;
import com.buddy.user.model.User;
import com.buddy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository repository;

    @Override
    public GetAdminUserListResponse getAdminUserList(
            String query, Integer limit, Integer offset, String[] roles
    ) {
        List<AdminUserListItemResponse> adminUserList = new ArrayList<>();

        List<AdminUserListRow> usersRow =
                repository.getAdminUserList(query, limit + 1, offset, roles);
        if (usersRow == null) throw new IllegalArgumentException("조회가 잘못 되었습니다.");

        Integer totalUserCount = repository.getTotalUserCount();
        if (totalUserCount == null) throw new IllegalArgumentException("조회가 잘못 되었습니다.");

        boolean hasNext = usersRow.size() > limit;
        List<AdminUserListRow> result =
                usersRow.subList(0, Math.min(limit, usersRow.size()));

        for (AdminUserListRow user : result) {
            Integer userAttendanceRate = getUserAttendanceRate(user.userId());
            AdminUserListItemResponse userListItemResponse = new AdminUserListItemResponse(
                    user.userId(),
                    user.name(),
                    user.nickname(),
                    user.level(),
                    userAttendanceRate,
                    user.recentSession(),
                    user.recentSessionTitle(),
                    user.status(),
                    user.role()
            );

            adminUserList.add(userListItemResponse);

        }

        return new GetAdminUserListResponse(
                adminUserList,
                query,
                limit,
                offset,
                hasNext,
                totalUserCount
        );

    }

    @Override
    public CreateAdminUserResponse createUserInAdminMode(CreateAdminUserRequest req) {

        LocalDateTime now = LocalDateTime.now();

        // 임시 이메일 구현
        String email = req.email();
        if (email == null || email.isBlank()) {
            email = "buddy_" + System.currentTimeMillis() + "@temp.local";
        }

        User user = new User(
                null,
                email,
                "SOCIAL_LOGIN_PENDING",
                req.name(),
                req.nickname(),
                req.gender(),
                req.role(),
                req.level(),
                4,
                req.status(),
                req.mbti(),
                req.jobs(),
                now,
                now
        );

        int result = repository.createUser(user);
        if (result == 0) throw new IllegalArgumentException("유저 저장 실패");

        return new CreateAdminUserResponse(
                user.id(),
                user.email(),
                user.name(),
                user.nickname(),
                user.gender(),
                user.role(),
                user.level(),
                user.status(),
                user.jobs(),
                user.mbti(),
                now.toString(),
                now.toString()
        );

    }

    @Override
    public GetAdminAttendanceAwardResponse getAdminAttendanceAward(Integer year, Integer month) {

        List<AttendanceAwardCandidateResponse> candidates = new ArrayList<>();
        List<AttendanceAwardCandidateRow> rows =
                repository.getAttendanceAwardCandidates(year, month);

        int rank = 1;
        for (AttendanceAwardCandidateRow row : rows) {
            candidates.add(new AttendanceAwardCandidateResponse(
                    rank,
                    row.userId(),
                    row.name(),
                    row.nickname(),
                    row.level(),
                    row.appliedCount(),
                    row.attendedCount(),
                    row.noShowCount(),
                    row.attendanceRate()
            ));

            rank += 1;
        }

        var summary = repository.getAttendanceSummary(year, month);
        if (summary == null) throw new IllegalArgumentException("출석 메타 정보 조회 실패");

        return new GetAdminAttendanceAwardResponse(year, month, summary, candidates);
    }

    @Override
    public Integer getUserAttendanceRate(Long userId) {
        return repository.getUserAttendanceRate(userId);
    }

    @Override
    public GetAdminUserDetailResponse getAdminUserDetail(Long userId) {
        AdminUserDetailRow user = repository.getAdminUserDetail(userId);
        if (user == null) throw new IllegalArgumentException("유저 정보가 없습니다.");

        LocalDateTime now = LocalDateTime.now();
        Integer year = now.getYear();
        Integer month = now.getMonth().getValue();

        // TODO : 뱃지기능 아직 구현 X
        Integer badgeCount = repository.countUserBadges(userId);
        if (badgeCount == null) badgeCount = 0;

        return new GetAdminUserDetailResponse(
                user.userId(),
                user.name(),
                user.nickname(),
                user.email(),
                user.role(),
                user.status(),
                user.level(),
                user.mbti(),
                user.jobs(),
                getMonthlyAttendanceResponse(userId, year, month),
                getRecentAttendedSessionResponse(userId),
                getMonthlyWordResponse(userId, year, month),
                badgeCount
        );
    }

    @Override
    public UpdateAdminUserRoleResponse updateUserRole(
            Long userId,
            UpdateAdminUserRoleRequest req
    ) {
        LocalDateTime now = LocalDateTime.now();
        Integer result = repository.updateUserRole(userId, req.role().name(), now);
        if (result == 0) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        return new UpdateAdminUserRoleResponse(userId, req.role());
    }

    @Override
    public UpdateAdminUserStatusResponse updateUserStatus(
            Long userId,
            UpdateAdminUserStatusRequest req
    ) {
        LocalDateTime now = LocalDateTime.now();
        Integer result = repository.updateUserStatus(userId, req.status().name(), now);
        if (result == 0) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        return new UpdateAdminUserStatusResponse(userId, req.status());
    }

    private AdminUserMonthlyAttendanceResponse getMonthlyAttendanceResponse(
            Long userId,
            Integer year,
            Integer month
    ) {
        AdminUserMonthlyAttendanceRow row = repository.getAdminUserMonthlyAttendance(userId, year, month);
        if (row == null) return new AdminUserMonthlyAttendanceResponse(
                0,
                0,
                0,
                0)
                ;

        return new AdminUserMonthlyAttendanceResponse(
                row.appliedCount(),
                row.attendedCount(),
                row.absentCount(),
                row.attendanceRate()
        );

    }

    private AdminUserRecentAttendedSessionResponse getRecentAttendedSessionResponse(Long userId) {
        AdminUserRecentAttendedSessionRow row = repository.getAdminUserRecentAttendedSession(userId);
        if (row == null) return null;

        return new AdminUserRecentAttendedSessionResponse(
                row.sessionId(),
                row.title(),
                row.location(),
                row.startsAt()
        );
    }

    private List<AdminUserMonthlyWordResponse> getMonthlyWordResponse(
            Long userId,
            Integer year,
            Integer month
    ) {
        List<AdminUserMonthlyWordResponse> responseList = new ArrayList<>();
        List<AdminUserMonthlyWordRow> rows = repository.getAdminUserMonthlyWords(userId, year, month);
        if (rows == null) return List.of();

        for (AdminUserMonthlyWordRow row : rows) {
            responseList.add(
                    new AdminUserMonthlyWordResponse(
                            row.wordEntryId(),
                            row.sessionId(),
                            row.phrase(),
                            row.example(),
                            row.submittedAt()
                    )
            );
        }

        return responseList;
    }
}
