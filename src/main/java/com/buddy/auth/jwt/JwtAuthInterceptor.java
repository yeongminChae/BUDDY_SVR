package com.buddy.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/auth") || requestUri.startsWith("/api/public")) {
            return true;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.startsWith("Bearer ") == false) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token) == false) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String role = jwtTokenProvider.getRole(token);

        if (requestUri.startsWith("/api/admin") && "ADMIN".equals(role) == false) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        if (requestUri.startsWith("/api/user")
                && ("USER".equals(role) == false && "ADMIN".equals(role) == false)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}