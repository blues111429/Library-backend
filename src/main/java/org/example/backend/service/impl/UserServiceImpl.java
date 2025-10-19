package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.*;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.example.backend.util.*;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    //mapper注入(构造方法)
    private static UserMapper userMapper;
    private static JwtUtil jwtUtil;
    public UserServiceImpl (UserMapper userMapper,  JwtUtil jwtUtil) {
        UserServiceImpl.userMapper = userMapper;
        UserServiceImpl.jwtUtil = jwtUtil;
    }

    //登录
    @Override
    public Result<LoginResponse> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getPhone());

        if( user == null ) { return Result.error("用户名不存在"); }

        if(userMapper.userStatus(user.getUsername()) <= 0) {return Result.error("该用户已被冻结，请联系管理员");}

        boolean match = PasswordUtil.matches(request.getPassword(), user.getPassword_hash());
        if(!match) { return Result.error("密码错误"); }

        //更新登录时间
        userMapper.updateLastLogin(user.getUser_id());
        //生成token
        String token = jwtUtil.generateToken(user.getUsername());

        LoginResponse response = LoginResponse.builder()
                                .userId(user.getUser_id())
                                .username(user.getUsername())
                                .typeCn(user.getType_cn())
                                .token(token).build();
        TokenStore.save(user.getUser_id(), token);
        return Result.success("登录成功", response);
    }
    //用户注册
    @Override
    public Result<RegisterResponse> register(RegisterRequest request) {
        //注册数据校验
        String message = UserTools.registerCheck(request);
        if(!message.isEmpty()) { return Result.error(message); }

        RegisterResponse response = new RegisterResponse();
        User user = UserTools.userRegister(request);

        if(userMapper.insert(user) <= 0) {return Result.error("注册失败");}
        //为新用户生成token
        String token =  jwtUtil.generateToken(user.getUsername());

        response.setMessage("注册成功");
        response.setUserId(user.getUser_id());
        response.setToken(token);
        return Result.success("注册成功", response);
    }
    //删除用户
    @Override
    public Result<String> deleteUser(DeleteRequest request) {

        Integer userId = request.getUserId();
        int result = userMapper.delete(userId);
        if(result <= 0) {
            return Result.error("未找到该用户");
        }

        return Result.success("删除成功");
    }
    //获取用户信息
    @Override
    public Result<UserInfoResponse> userInfo(HttpServletRequest httpRequest) {
        //登录校验
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        String token  = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        User user = userMapper.findByUsername(username);
        if( user == null ) { return Result.error("没有找到该用户"); }
        UserInfoResponse response = UserInfoResponse.builder()
                                    .username(user.getUsername())
                                    .name(user.getName())
                                    .typeCn(user.getType_cn())
                                    .gender(user.getGender())
                                    .phone(user.getPhone())
                                    .email(user.getEmail())
                                    .build();
        return Result.success("获取成功", response);
    }
    //退出登录
    @Override
    public Result<LogoutResponse> logout(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未提供token,退出失败");
        }
        String token = authHeader.substring(7);
        TokenBlacklist.add(token);
        return Result.success("退出成功");
    }

}
