package org.example.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminLog {
    private Integer log_id;
    private Integer admin_id;
    private String action;
    private LocalDateTime create_time;
}
