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
    //删除用户
    Result<String> deleteUser(DeleteRequest request, HttpServletRequest httpRequest);
    //用户信息
    Result<UserInfoResponse> userInfo(HttpServletRequest httpRequest);
    //修改用户信息
    Result<String> updateUserInfo(UpdateUserInfoRequest request, HttpServletRequest httpRequest);
    //退出登录
    Result<LogoutResponse> logout(HttpServletRequest httpRequest);
    //修改密码
    Result<String> resetPassword(HttpServletRequest httpRequest);

    //新增用户
    Result<String> addUser(RegisterRequest request, HttpServletRequest httpRequest);
    //用户列表
    Result<List<UserListResponse>> userList(HttpServletRequest httpRequest);
    //更新用户状态
    Result<String> updateStatus(UpdateUserStatusRequest request, HttpServletRequest httpRequest);
    //编辑用户
    Result<String> editUser(EditUserRequest request, HttpServletRequest httpRequest);
}
