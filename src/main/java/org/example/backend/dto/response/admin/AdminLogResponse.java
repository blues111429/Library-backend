package org.example.backend.dto.response.admin;

import lombok.Data;
import org.example.backend.model.AdminLog;

import java.util.List;

@Data
public class AdminLogResponse {
    private List<AdminLog> logs;
}
