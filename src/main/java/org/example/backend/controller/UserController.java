package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    //构造方法注入
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    //用户登录
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) { return userService.login(request); }

    //用户注册
    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody RegisterRequest request) { return userService.register(request);}

    //删除
    @PostMapping("/deleteUser")
    public Result<String> delete(@RequestBody DeleteRequest request, HttpServletRequest httpRequest) { return userService.deleteUser(request, httpRequest); }

    //修改信息
    @PostMapping("/updateUserInfo")
    public Result<String> updateUserInfo(@RequestBody UpdateUserInfoRequest request, HttpServletRequest servletRequest) { return userService.updateUserInfo(request, servletRequest); }

    //用户查看个人信息
    @GetMapping("/userInfo")
    public Result<UserInfoResponse> userInfo(HttpServletRequest httpRequest) { return userService.userInfo(httpRequest); }

    //退出登录
    @PostMapping("/logout")
    public Result<LogoutResponse> logout(HttpServletRequest httpRequest) { return userService.logout(httpRequest); }

    //修改密码
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(HttpServletRequest httpRequest) { return userService.resetPassword(httpRequest); }
}
