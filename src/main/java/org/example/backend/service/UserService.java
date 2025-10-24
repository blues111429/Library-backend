package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.*;
import org.springframework.stereotype.Service;

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
}
