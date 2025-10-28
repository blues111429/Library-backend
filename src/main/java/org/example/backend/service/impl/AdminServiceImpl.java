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
import org.example.backend.service.AdminService;
import org.example.backend.util.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {
    //mapper注入(构造方法)
    private static UserMapper userMapper;

    public AdminServiceImpl(UserMapper userMapper) {
        AdminServiceImpl.userMapper = userMapper;
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
        UserTools.adminLog(httpRequest, "新增用户(用户手机号:):"+request.getPhone());

        return Result.success("新增成功");
    }
    //获取用户列表
    @Override
    public Result<List<UserListResponse>> userList(HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        //获取管理员ID
        Integer adminId = UserTools.getUserIdFromRequest(httpRequest);

        List<User> users = userMapper.userList(adminId);
        List<UserListResponse> userListResponse = new ArrayList<>();
        for(User user : users) {
            UserListResponse response = UserTools.getUserListResponse(user);
            userListResponse.add(response);
        }
        UserTools.adminLog(httpRequest, "获取用户列表");
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
        System.out.println("修改前用户:" + oldUser);
        if(oldUser == null) {return Result.error("该用户不存在");}
        if(request.getPhone() !=  null && !request.getPhone().equals(oldUser.getPhone())) {
            if(userMapper.findByOnlyUsername(request.getPhone()) != null) {
                return Result.error("该手机号已被使用");
            }
        }

        User user = UserTools.newUser(request);
        System.out.println(request);
        if(userMapper.editUser(request) <= 0) {
            return Result.error("编辑失败");
        }

        StringBuilder logBuilder = new StringBuilder("编辑用户ID:" + oldUser.getUser_id() + "修改字段：");
        if(request.getName() != null && !request.getName().equals(oldUser.getName())) {
            logBuilder.append("用户姓名从[").append(oldUser.getName()).append("]改为[").append(request.getName()).append("]");
        }
        if(request.getPhone() != null && !request.getPhone().equals(oldUser.getPhone())) {
            logBuilder.append("用户手机号从[").append(oldUser.getPhone()).append("]改为[").append(request.getPhone()).append("]");
        }
        if(request.getEmail() != null && !request.getEmail().equals(oldUser.getEmail())) {
            logBuilder.append("用户邮箱从[").append(oldUser.getEmail()).append("]改为[").append(request.getEmail()).append("]");
        }
        if(request.getGender() != null && !request.getGender().equals(oldUser.getGender())) {
            logBuilder.append("用户性别从[").append(oldUser.getGender()).append("]改为[").append(request.getGender()).append("]");
        }
        if(request.getType() != null && !request.getType().equals(oldUser.getType())) {
            logBuilder.append("用户类别从[").append(oldUser.getType()).append("]改为[").append(request.getType()).append("]");
        }

        UserTools.adminLog(httpRequest, logBuilder.toString());
        return Result.success("编辑成功");
    }
}
