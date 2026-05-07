package com.buddy.table.controller;

import com.buddy.common.web.ApiResponse;
import com.buddy.table.model.TableAssignment;
import com.buddy.table.service.UserTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/tables")
public class UserTableController {

    private final UserTableService userTableService;

    @GetMapping("/my")
    public ApiResponse<TableAssignment> my(@RequestParam Long sessionId, @RequestParam Long userId) {
        return ApiResponse.ok(userTableService.myAssignment(sessionId, userId));
    }

}
