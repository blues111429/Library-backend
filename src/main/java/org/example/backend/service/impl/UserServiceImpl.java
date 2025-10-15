package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.user.*;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.example.backend.util.JwtUtil;
import org.example.backend.util.PasswordUtil;
import org.example.backend.util.TokenBlacklist;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    //mapper注入(构造方法)
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    public UserServiceImpl (UserMapper userMapper,  JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    //登录
    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        User user = userMapper.findByUsername(request.getUsername());
        if( user == null ) {
            response.setMessage("用户名不存在");
            return response;
        }

        boolean match = PasswordUtil.matches(request.getPassword(), user.getPassword_hash());
        if(!match) {
            response.setMessage("密码错误");
            return response;
        }

        userMapper.updateLastLogin(user.getUser_id());
        String token = jwtUtil.generateToken(user.getUsername());

        response.setMessage("登录成功");
        response.setUserId(user.getUser_id());
        response.setUsername(user.getUsername());
        response.setTypeCn(user.getType_cn());
        response.setToken(token);
        return response;
    }

    //注册
    @Override
    public RegisterResponse register(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            response.setMessage("用户名已存在");
            return response;
        }

        String encryptedPassword = PasswordUtil.encrypt(request.getPassword());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword_hash(encryptedPassword);
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setType(request.getType());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);

        if(userMapper.insert(user) <= 0) {
            response.setMessage("注册失败");
            return response;
        }

        String token =  jwtUtil.generateToken(user.getUsername());

        response.setMessage("注册成功");
        response.setUserId(user.getUser_id());
        response.setToken(token);
        return response;
    }

    //删除用户
    @Override
    public DeleteResponse delete(DeleteRequest request) {
        Integer userId = request.getUserId();
        int result = userMapper.delete(userId);
        DeleteResponse response = new DeleteResponse();
        if(result <= 0) {
            response.setMessage("未找到该用户");
        } else {
            response.setMessage("删除成功");
        }
        return response;
    }

    //获取用户信息
    @Override
    public UserInfoResponse userInfo(HttpServletRequest httpRequest) {
        String authHeader  = httpRequest.getHeader("Authorization");
        System.out.println("Received authHeader : " + authHeader);
        if(authHeader  == null || !authHeader .startsWith("Bearer ")) {
            throw new RuntimeException("未登录或登录已过期，请先登录");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        UserInfoResponse response = new UserInfoResponse();
        if( user == null ) {
            response.setMessage("没有找到该用户");
            return response;
        }
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setTypeCn(user.getType_cn());
        response.setGender(user.getGender());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setMessage("获取成功");

        return response;
    }

    //获取用户列表
    @Override
    public List<UserListResponse> userList() {
        List<User> users = userMapper.userList();
        List<UserListResponse> userListResponse = new ArrayList<>();
        for(User user : users) {
            UserListResponse response = new UserListResponse();
            response.setUser_id(user.getUser_id());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            response.setGender(user.getGender());
            response.setTypeCn(user.getType_cn());
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
            response.setStatus(user.getStatus());
            response.setCreate_time(user.getCreate_time());
            response.setLast_login(user.getLast_login());
            userListResponse.add(response);
        }
        return userListResponse;
    }

    //退出登录
    @Override
    public LogoutResponse logout(LogoutRequest logoutRequest, HttpServletRequest httpRequest) {
        LogoutResponse response = new LogoutResponse();

        String authHeader = httpRequest.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            TokenBlacklist.add(token);
            response.setMessage("退出成功");
        } else {
            response.setMessage("未提供token， 退出失败");
        }
        return response;
    }
}
