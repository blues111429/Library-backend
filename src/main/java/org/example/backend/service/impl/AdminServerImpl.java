package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.EditUserRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.request.user.UpdateUserStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.UserListResponse;
import org.example.backend.mapper.AdminMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.AdminServer;
import org.example.backend.util.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AdminServerImpl implements AdminServer {
    //mapper注入(构造方法)
    private static UserMapper userMapper;
    private static AdminMapper adminMapper;

    public AdminServerImpl (UserMapper userMapper, AdminMapper adminMapper) {
        AdminServerImpl.userMapper = userMapper;
        AdminServerImpl.adminMapper = adminMapper;
    }

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

        Integer adminId = UserTools.getUserIdFromRequest(httpRequest);
        adminMapper.insertLog(adminId, "新增用户:" + request.getPhone());

        return Result.success("新增成功");
    }
    //获取用户列表
    @Override
    public Result<List<UserListResponse>> userList(HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        List<User> users = userMapper.userList();
        System.out.println(users);
        List<UserListResponse> userListResponse = new ArrayList<>();
        for(User user : users) {
            UserListResponse response = UserTools.getUserListResponse(user);
            userListResponse.add(response);
        }

        Integer adminId = UserTools.getUserIdFromRequest(httpRequest);
        adminMapper.insertLog(adminId, "获取用户列表");

        return Result.success("获取用户列表成功",userListResponse);
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

            Integer adminId = UserTools.getUserIdFromRequest(httpRequest);
            String action = (newStatus == 1 ? "启用" : "禁用") + "用户(用户ID):" + userId;
            adminMapper.insertLog(adminId, action);

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

        User user = UserTools.newUser(request);
        System.out.println("当前被编辑用户:"+user);
//        userMapper.editUser(user);

        return Result.success("编辑成功");
    }
}
