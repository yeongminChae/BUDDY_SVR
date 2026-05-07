package com.buddy.auth.repository;

import com.buddy.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final AuthMapper mapper;

//    public AuthRepositoryImpl(AttendanceMapper mapper) {
//        this.mapper = mapper;
//    }

//    @Override
//    public void upsert(Attendance attendance) {
//        mapper.upsert(attendance);
//    }

//    @Override
//    public List<Attendance> findBySession(Long sessionId) {
//        return mapper.findBySession(sessionId);
//    }
}
