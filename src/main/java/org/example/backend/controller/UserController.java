package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.*;
import org.example.backend.dto.response.user.*;
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

    //登录
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) { return userService.login(request); }

    //注册
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){
        return userService.register(request);
    }

    //删除
    @PostMapping("/delete")
    public DeleteResponse delete(@RequestBody DeleteRequest request){ return userService.delete(request); }

    //用户信息
    @GetMapping("/userInfo")
    public UserInfoResponse userInfo(HttpServletRequest httpRequest){ return userService.userInfo(httpRequest); }

    //用户列表
    @GetMapping("/userList")
    public List<UserListResponse> userList(){ return userService.userList(); }

    //退出登录
    @PostMapping("/logout")
    public LogoutResponse logout(@RequestBody LogoutRequest request, HttpServletRequest httpRequest){ return userService.logout(request,httpRequest); }
}
