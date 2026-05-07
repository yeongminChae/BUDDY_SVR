package com.buddy.session.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Session {
    private Long id;
    private LocalDateTime startsAt;
    private String location;
    private String topic;
    private String type;
    private Integer attendanceCount;
    private Integer capacity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String title;
    private Long hostId;
    private Integer cost;
}
