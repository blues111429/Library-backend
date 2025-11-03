package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.admin.AdminLogResponse;
import org.example.backend.mapper.AdminMapper;
import org.example.backend.model.AdminLog;
import org.example.backend.service.AdminService;
import org.example.backend.util.*;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {
    //mapper注入(构造方法)
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }
    //查看管理员操作日志
    public Result<AdminLogResponse> adminLog(HttpServletRequest httpRequest) {
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        List<AdminLog> logs = adminMapper.getAllLog();
        if(logs == null || logs.isEmpty()) {
            return Result.error("暂无操作日志");
        }

        AdminLogResponse adminLogResponse = new AdminLogResponse();
        adminLogResponse.setLogs(logs);

        return Result.success(adminLogResponse);
    }
}
