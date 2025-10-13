package org.example.backend.service;

import org.example.backend.dto.request.LoginRequest;
import org.example.backend.dto.request.RegisterRequest;
import org.example.backend.dto.response.LoginResponse;
import org.example.backend.dto.response.RegisterResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    //登录
    LoginResponse login( LoginRequest request);
    //注册
    RegisterResponse register(RegisterRequest request);
}
