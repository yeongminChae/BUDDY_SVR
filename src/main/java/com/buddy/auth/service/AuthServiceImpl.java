package com.buddy.auth.service;

import com.buddy.auth.dto.LoginRequest;
import com.buddy.auth.dto.LoginResponse;
import com.buddy.auth.jwt.JwtTokenProvider;
import com.buddy.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository repository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest req) {
        if ("admin".equals(req.loginId()) && "1234".equals(req.password())) {
            String accessToken = jwtTokenProvider.createToken("admin", "ADMIN");
            return new LoginResponse(accessToken, "ADMIN");
        }

        if ("user".equals(req.loginId()) && "1234".equals(req.password())) {
            String accessToken = jwtTokenProvider.createToken("user", "USER");
            return new LoginResponse(accessToken, "USER");
        }

        throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

}
