package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.*;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.example.backend.util.JwtUtil;
import org.example.backend.util.PasswordUtil;
import org.example.backend.util.TokenBlacklist;
import org.example.backend.util.TokenStore;
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
    public Result<LoginResponse> login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());

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

    //注册
    @Override
    public Result<RegisterResponse> register(RegisterRequest request) {

        //用户名重复校验
        User existing = userMapper.findByOnlyUsername(request.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        //手机号校验
        if (request.getPhone() == null || !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }
        //邮箱校验
        if(request.getEmail()==null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return Result.error("邮箱格式不对");
        }
        //密码校验

        RegisterResponse response = new RegisterResponse();
        User user = userRegister(request);

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
    public Result<UserInfoResponse> userInfo(HttpServletRequest httpRequest) {
        String message = tokenCheck(httpRequest);
        if(!message.isEmpty()) {
            return Result.error(message);
        }

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

    //获取用户列表(管理员)
    @Override
    public Result<List<UserListResponse>> userList(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        //未登录
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未授权访问,请先登录");
        }
        //去掉‘Bearer’
        token = token.substring(7);
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return Result.error("Token已无效或已过期,请重新登陆");
        }
        //查找当前用户
        User currentUser = userMapper.findByUsername(username);
        if(currentUser == null) { return Result.error("当前用户不存在"); }

        if(!"管理员".equals(currentUser.getType_cn())) { throw new RuntimeException("权限不足，仅管理员可以访问"); }

        List<User> users = userMapper.userList();
        List<UserListResponse> userListResponse = new ArrayList<>();
        for(User user : users) {
            UserListResponse response = getUserListResponse(user);
            userListResponse.add(response);
        }
        return Result.success("获取用户列表成功",userListResponse);
    }

    //更新账号状态(管理员)
    @Override
    public Result<String> updateStatus(UpdateUserStatusRequest request, HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未授权访问,请先登录");
        }

        int userId = request.getUserId();
        int newStatus = request.getStatus();

        if(userMapper.updateUserStatus(request.getUserId(), request.getStatus()) > 0) {

            if(newStatus <= 0) {
                String userToken = TokenStore.get(userId);
                if(userToken != null) {
                    TokenBlacklist.add(userToken);
                    TokenStore.remove(userId);
                }
            }
            return Result.success("用户状态更新成功");
        } else {
            return Result.error("用户状态更新失败");
        }
    }

    //未登录检验
    private static String tokenCheck(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        String message = "";
        if(token == null || !token.startsWith("Bearer ")) {
            message = "未授权访问，请先登录";
        }
        return message;
    }

    //设置用户注册
    private static User userRegister(RegisterRequest request) {
        //密码加密
        String encryptedPassword = PasswordUtil.encrypt(request.getPassword());
        //创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword_hash(encryptedPassword);
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setType(request.getType());
        user.setType_cn(request.getType());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        return user;
    }

    //设置获取用户列表返回的response
    private static UserListResponse getUserListResponse(User user) {
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
        return response;
    }
}
