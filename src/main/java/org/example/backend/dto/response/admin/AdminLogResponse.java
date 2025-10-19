package org.example.backend.dto.response.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminLogResponse {
    private Integer logId;
    private Integer adminId;
    private String adminName;
    private String action;
    private LocalDateTime createTime;
}
