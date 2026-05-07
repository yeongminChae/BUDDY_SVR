package com.buddy.user.controller;

import com.buddy.common.web.ApiResponse;
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
import com.buddy.user.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping()
    public ResponseEntity<ApiResponse<GetAdminUserListResponse>> getAdminUserList(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false) String[] roles
    ) {
        GetAdminUserListResponse response =
                service.getAdminUserList(query, limit, offset, roles);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<GetAdminUserDetailResponse>> getAdminUserDetail(
            @PathVariable Long userId
    ) {
        try {
            GetAdminUserDetailResponse response = service.getAdminUserDetail(userId);

            return ResponseEntity.ok(ApiResponse.ok(response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("GET_ADMIN_USER_DETAIL_FAILED", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."));

        }
    }

    @GetMapping("/attendance-awards")
    public ResponseEntity<ApiResponse<GetAdminAttendanceAwardResponse>>
    getAdminAttendanceAward(
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        GetAdminAttendanceAwardResponse response =
                service.getAdminAttendanceAward(year, month);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CreateAdminUserResponse>> createAdminUser(
            @Valid @RequestBody CreateAdminUserRequest req
    ) {
        CreateAdminUserResponse response = service.createUserInAdminMode(req);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UpdateAdminUserRoleResponse>> updateAdminUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAdminUserRoleRequest req
    ) {
        UpdateAdminUserRoleResponse response = service.updateUserRole(userId, req);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UpdateAdminUserStatusResponse>> updateAdminUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAdminUserStatusRequest req
    ) {
        UpdateAdminUserStatusResponse response = service.updateUserStatus(userId, req);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
