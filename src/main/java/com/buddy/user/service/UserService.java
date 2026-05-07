package com.buddy.user.service;

import com.buddy.user.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long id);
}
