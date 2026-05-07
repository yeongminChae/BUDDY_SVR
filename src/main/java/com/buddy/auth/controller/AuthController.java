package com.buddy.auth.controller;

import com.buddy.auth.dto.LoginRequest;
import com.buddy.auth.dto.LoginResponse;
import com.buddy.auth.service.AuthService;
import com.buddy.common.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest req
    ) {
        LoginResponse response = service.login(req);
        return ApiResponse.ok(response);
    }

}
