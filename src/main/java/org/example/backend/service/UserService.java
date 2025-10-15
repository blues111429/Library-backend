package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.user.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {
    //登录
    LoginResponse login(LoginRequest request);
    //注册
    RegisterResponse register(RegisterRequest request);

    //删除用户
    DeleteResponse delete(DeleteRequest request);

    //用户信息
    UserInfoResponse userInfo(HttpServletRequest httpRequest);

    //获取用户列表
    List<UserListResponse> userList();

    //退出登录
    LogoutResponse logout(LogoutRequest request, HttpServletRequest httpRequest);
}
