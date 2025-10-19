package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.request.user.UpdateUserStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.UserListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminServer {
    //新增用户
    Result<String> addUser(RegisterRequest request, HttpServletRequest httpServlet);
    //获取用户列表
    Result<List<UserListResponse>> userList(HttpServletRequest httpRequest);
    //更新账号状态
    Result<String> updateStatus(UpdateUserStatusRequest request, HttpServletRequest httpRequest);
}
