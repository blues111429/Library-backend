package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.EditUserRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.request.user.UpdateUserStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.UserListResponse;
import org.example.backend.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) { this.adminService = adminService; }
    //新增用户
    @PostMapping("/addUser")
    public Result<String> addUser(@RequestBody RegisterRequest request, HttpServletRequest httpServlet) { return adminService.addUser(request,httpServlet); }
    //获取用户列表
    @GetMapping("/userList")
    public Result<List<UserListResponse>> userList(HttpServletRequest httpServlet) { return adminService.userList(httpServlet); }
    //更新用户账号状态
    @PostMapping("/updateUserStatus")
    public Result<String> updateUserStatus(@RequestBody UpdateUserStatusRequest request, HttpServletRequest httpRequest) {return adminService.updateStatus(request, httpRequest);}
    //编辑用户信息
    @PostMapping("/editUser")
    public Result<String> editUser(@RequestBody EditUserRequest request, HttpServletRequest httpRequest) { return adminService.editUser(request, httpRequest); }
}
