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
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){
        return userService.register(request);
    }

    @PostMapping("/delete")
    public DeleteResponse delete(@RequestBody DeleteRequest request){ return userService.delete(request); }

    @PostMapping("/userInfo")
    public UserInfoResponse userInfo(@RequestBody UserInfoRequest request){ return userService.userInfo(request); }

    @GetMapping("/userList")
    public List<UserListResponse> userList(){ return userService.userList(); }

    @PostMapping("/logout")
    public LogoutResponse logout(@RequestBody LogoutRequest request, HttpServletRequest httpRequest){ return userService.logout(request,httpRequest); }
}
