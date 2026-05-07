package com.buddy.user.service;

import com.buddy.table.dto.update.UpdateTableAssignmentsRequest;
import com.buddy.user.dto.attendanceAward.GetAdminAttendanceAwardResponse;
import com.buddy.user.dto.create.CreateAdminUserRequest;
import com.buddy.user.dto.create.CreateAdminUserResponse;
import com.buddy.user.dto.patch.UpdateAdminUserRoleRequest;
import com.buddy.user.dto.patch.UpdateAdminUserRoleResponse;
import com.buddy.user.dto.patch.UpdateAdminUserStatusRequest;
import com.buddy.user.dto.patch.UpdateAdminUserStatusResponse;
import com.buddy.user.dto.userDetail.GetAdminUserDetailResponse;
import com.buddy.user.dto.userList.GetAdminUserListResponse;
import jakarta.validation.Valid;

public interface AdminUserService {

    GetAdminUserListResponse getAdminUserList(
            String query,
            Integer limit,
            Integer offset,
            String[] roles
    );

    CreateAdminUserResponse createUserInAdminMode(CreateAdminUserRequest req);

    GetAdminAttendanceAwardResponse getAdminAttendanceAward(Integer year, Integer month);

    Integer getUserAttendanceRate(Long userId);

    GetAdminUserDetailResponse getAdminUserDetail(Long userId);

    UpdateAdminUserRoleResponse updateUserRole(Long userId, @Valid UpdateAdminUserRoleRequest req);

    UpdateAdminUserStatusResponse updateUserStatus(Long userId, @Valid UpdateAdminUserStatusRequest req);

}

