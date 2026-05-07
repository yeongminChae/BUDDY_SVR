package com.buddy.auth.service;

import com.buddy.auth.dto.LoginRequest;
import com.buddy.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest req);

}
