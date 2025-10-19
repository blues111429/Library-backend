package org.example.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminLog {
    private Long logId;
    private Long adminId;
    private String action;
    private LocalDateTime createTime;
}
