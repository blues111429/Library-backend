package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.request.user.UpdateUserStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.user.UserListResponse;
import org.example.backend.service.AdminServer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminServer adminServer;

    public AdminController(AdminServer adminServer) { this.adminServer = adminServer; }
    //新增用户
    @PostMapping("/addUser")
    public Result<String> addUser(@RequestBody RegisterRequest request, HttpServletRequest httpServlet) { return adminServer.addUser(request,httpServlet); }
    //获取用户列表
    @GetMapping("/userList")
    public Result<List<UserListResponse>> userList(HttpServletRequest httpServlet) { return adminServer.userList(httpServlet); }
    //更新用户账号状态(管理员)
    @PostMapping("/updateUserStatus")
    public Result<String> updateUserStatus(@RequestBody UpdateUserStatusRequest request, HttpServletRequest httpRequest) {return adminServer.updateStatus(request, httpRequest);}
}
