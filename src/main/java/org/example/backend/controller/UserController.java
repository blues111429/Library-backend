package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.user.*;
import org.example.backend.dto.response.Result;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    //构造方法注入
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //用户登录
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) { return userService.login(request); }

    //用户注册
    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody RegisterRequest request){ return userService.register(request);}

    //新增用户(管理员)
    @PostMapping("/addUser")
    public Result<String> addUser(@RequestBody RegisterRequest request, HttpServletRequest httpServlet) { return userService.addUser(request,httpServlet); }

    //删除
    @PostMapping("/delete")
    public DeleteResponse delete(@RequestBody DeleteRequest request){ return userService.delete(request); }

    //用户查看个人信息
    @GetMapping("/userInfo")
    public Result<UserInfoResponse> userInfo(HttpServletRequest httpRequest){ return userService.userInfo(httpRequest); }

    //用户列表(管理员)
    @GetMapping("/userList")
    public Result<List<UserListResponse>> userList(HttpServletRequest httpRequest){ return userService.userList(httpRequest); }

    //更新用户账号状态(管理员)
    @PostMapping("/updateUserStatus")
    public Result<String> updateUserStatus(@RequestBody UpdateUserStatusRequest request, HttpServletRequest httpRequest) {return userService.updateStatus(request, httpRequest);}

    //退出登录
    @PostMapping("/logout")
    public Result<LogoutResponse> logout(HttpServletRequest httpRequest){ return userService.logout(httpRequest); }
}
