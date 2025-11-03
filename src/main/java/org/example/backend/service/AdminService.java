package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.admin.AdminLogResponse;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    //查看管理员操作日志
    Result<AdminLogResponse> adminLog(HttpServletRequest httpRequest);
}
