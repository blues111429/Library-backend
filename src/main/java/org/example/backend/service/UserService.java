package org.example.backend.service;

import org.example.backend.dto.request.DeleteRequest;
import org.example.backend.dto.request.LoginRequest;
import org.example.backend.dto.request.RegisterRequest;
import org.example.backend.dto.request.UserInfoRequest;
import org.example.backend.dto.response.DeleteResponse;
import org.example.backend.dto.response.LoginResponse;
import org.example.backend.dto.response.RegisterResponse;
import org.example.backend.dto.response.UserInfoResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    //登录
    LoginResponse login( LoginRequest request);
    //注册
    RegisterResponse register(RegisterRequest request);

    //删除用户
    DeleteResponse delete(DeleteRequest request);

    //用户信息
    UserInfoResponse userInfo(UserInfoRequest request);
}
