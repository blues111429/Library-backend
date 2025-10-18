package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {
    //登录
    Result<LoginResponse> login(LoginRequest request);
    //注册
    Result<RegisterResponse> register(RegisterRequest request);
    //新增用户
    Result<String> addUser(RegisterRequest request, HttpServletRequest httpServlet);
    //删除用户
    DeleteResponse delete(DeleteRequest request);
    //用户信息
    Result<UserInfoResponse> userInfo(HttpServletRequest httpRequest);
    //获取用户列表
    Result<List<UserListResponse>> userList(HttpServletRequest httpRequest);
    //更新账号状态
    Result<String> updateStatus(UpdateUserStatusRequest request, HttpServletRequest httpRequest);
    //退出登录
    Result<LogoutResponse> logout(HttpServletRequest httpRequest);
}
