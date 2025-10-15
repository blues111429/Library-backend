package org.example.backend.service;

import org.example.backend.dto.request.DeleteRequest;
import org.example.backend.dto.request.LoginRequest;
import org.example.backend.dto.request.RegisterRequest;
import org.example.backend.dto.request.UserInfoRequest;
import org.example.backend.dto.response.*;
import org.springframework.stereotype.Service;
import java.util.List;

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

    //获取用户列表
    List<UserListResponse> userList();
}
