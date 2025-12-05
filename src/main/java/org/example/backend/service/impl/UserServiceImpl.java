package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.*;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.FieldValue;
import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.example.backend.util.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        //查询用户
        User user = userMapper.findByUsername(request.getPhone());
        //是否存在
        if(user == null) { return Result.error("用户不存在"); }
        System.out.println("当前登录用户:"+user);
        //检查状态
        if(userMapper.userStatus(user.getUsername()) <= 0) {return Result.error("该用户已被冻结或被删除，请联系管理员");}
        else { System.out.println("用户状态正常"); }
        //账密
        boolean match = PasswordUtil.matches(request.getPassword(), user.getPassword_hash());
        if(!match) { return Result.error("密码错误"); }
        //更新登录时间
        userMapper.updateLastLogin(user.getUser_id());
        //生成token
        String token = jwtUtil.generateToken(user.getUsername());
        //返回回应
        LoginResponse response = LoginResponse.builder()
                                .name(user.getName())
                                .userId(user.getUser_id())
                                .username(user.getUsername())
                                .typeCn(user.getType_cn())
                                .token(token).build();
        //保存生成的Token
        TokenStore.save(user.getUser_id(), token);
        return Result.success("登录成功", response);
    }
    //用户注册
    @Override
    public Result<RegisterResponse> register(RegisterRequest request) {
        //注册数据校验
        String message = UserTools.registerCheck(request);
        if(!message.isEmpty()) { return Result.error(message); }
        //新增用户
        User user = UserTools.userRegister(request);//创建新用户
        if(userMapper.insert(user) <= 0) {return Result.error("注册失败");}

        RegisterResponse response = RegisterResponse.builder()
                .message("注册成功")
                .userId(user.getUser_id())
                .build();
        return Result.success("注册成功", response);
    }
    //删除用户(暂不做更改）
    @Override
    public Result<String> deleteUser(DeleteRequest request, HttpServletRequest httpRequest) {
        //判断执行该操作用户是否为登录状态
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        //删除token
        TokenStore.remove(request.getUserId());
        int result = userMapper.delete(request.getUserId());
        if(result <= 0) {
            return Result.error("未找到该用户");
        }
        UserTools.adminLog(httpRequest, "删除用户(用户ID):"+request.getUserId());
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
                .user_id(user.getUser_id())
                .username(user.getUsername())
                .name(user.getName())
                .typeCn(user.getType_cn())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
        return Result.success("获取成功", response);
    }
    //更新用户信息
    @Override
    public Result<String> updateUserInfo(UpdateUserInfoRequest request, HttpServletRequest httpRequest) {
        //登录校验
        String message = UserTools.tokenCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        System.out.println(request.getUser_id());
        if(userMapper.updateUserInfo(request) <= 0) {
            return Result.error("修改失败");
        }
        return Result.success("修改成功");
    }
    //退出登录
    @Override
    public Result<LogoutResponse> logout(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) { return Result.error("未提供token,退出失败"); }
        String token = authHeader.substring(7);
        TokenBlacklist.add(token);
        return Result.success("退出成功,正在跳转...");
    }
    //管理员
    //新增用户
    @Override
    public Result<String> addUser(RegisterRequest request, HttpServletRequest httpRequest) {
        //管理员身份校验
        String adminMessage = UserTools.adminCheck(httpRequest);
        if(!adminMessage.isEmpty()) { return Result.error(adminMessage); }
        //注册数据校验
        String registerMessage = UserTools.registerCheck(request);
        if(!registerMessage.isEmpty()) { return Result.error(registerMessage); }

        User user = UserTools.userRegister(request);

        if(userMapper.insert(user) <= 0) { return Result.error("新增失败"); }
        UserTools.adminLog(httpRequest, "新增用户(用户手机号:):"+request.getPhone());

        return Result.success("新增成功");
    }
    //获取用户列表
    @Override
    public Result<List<UserListResponse>> userList(HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if (!message.isEmpty()) {
            return Result.error(message);
        }
        //获取管理员ID
        Integer adminId = UserTools.getUserIdFromRequest(httpRequest);

        List<User> users = userMapper.userList(adminId);
        List<UserListResponse> userListResponse = new ArrayList<>();
        for (User user : users) {
            UserListResponse response = UserTools.getUserListResponse(user);
            userListResponse.add(response);
        }
        UserTools.adminLog(httpRequest, "获取用户列表");
        return Result.success("获取用户列表成功", userListResponse);
    }
    //更新账号状态
    @Override
    public Result<String> updateStatus(UpdateUserStatusRequest request, HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

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
            UserTools.adminLog(httpRequest, (newStatus == 1 ? "启用" : "禁用") + "用户(用户ID):" + userId);

            return Result.success("用户状态更新成功");
        } else {
            return Result.error("用户状态更新失败");
        }
    }
    //编辑用户
    @Override
    public Result<String> editUser(EditUserRequest request, HttpServletRequest httpRequest) {
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        User oldUser = userMapper.findUserById(request.getUser_id());
        if(oldUser == null) {return Result.error("该用户不存在");}
        System.out.println("修改前用户:" + oldUser);
        if(request.getPhone() !=  null && !request.getPhone().equals(oldUser.getPhone())) {
            if(userMapper.findByOnlyUsername(request.getPhone()) != null) {
                return Result.error("该手机号已被使用");
            }
        }
        if(userMapper.editUser(request) <= 0) {
            return Result.error("编辑失败");
        }
        //编辑日志
        List<FieldValue> userFields = Arrays.asList(
                new FieldValue("姓名", oldUser.getName(), request.getName()),
                new FieldValue("手机号", oldUser.getPhone(), request.getPhone()),
                new FieldValue("邮箱", oldUser.getEmail(), request.getEmail()),
                new FieldValue("性别", oldUser.getGender(), request.getGender()),
                new FieldValue("类别", oldUser.getType(), request.getType())
        );
        Map<String, Object> oldValues = userFields.stream().collect(Collectors.toMap(FieldValue::getField, FieldValue::getOldValue));
        Map<String, Object> newValues = userFields.stream().collect(Collectors.toMap(FieldValue::getField, FieldValue::getNewValue));
        String log = LogEditor.generateEditLog("编辑用户", String.valueOf(oldUser.getUser_id()), oldValues, newValues);
        UserTools.adminLog(httpRequest, log);

        return Result.success("编辑成功");
    }
}
