package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.book.AddBookRequest;
import org.example.backend.dto.request.book.EditBookRequest;
import org.example.backend.dto.request.user.EditUserRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.request.user.UpdateUserStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.admin.AdminLogResponse;
import org.example.backend.dto.response.user.UserListResponse;
import org.example.backend.service.AdminService;
import org.example.backend.service.BookService;
import org.example.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final BookService bookService;
    public AdminController(AdminService adminService, UserService userService, BookService bookService) {
        this.adminService = adminService;
        this.userService = userService;
        this.bookService = bookService;

    }
    //adminService
    //查看管理员操作日志
    @GetMapping("/logs")
    public Result<AdminLogResponse> adminLogs(HttpServletRequest httpRequest) { return adminService.adminLog(httpRequest); }

    //userService
    //新增用户
    @PostMapping("/addUser")
    public Result<String> addUser(@RequestBody RegisterRequest request, HttpServletRequest httpServlet) { return userService.addUser(request,httpServlet); }
    //获取用户列表
    @GetMapping("/userList")
    public Result<List<UserListResponse>> userList(HttpServletRequest httpServlet) { return userService.userList(httpServlet); }
    //更新用户账号状态
    @PostMapping("/updateUserStatus")
    public Result<String> updateUserStatus(@RequestBody UpdateUserStatusRequest request, HttpServletRequest httpRequest) {return userService.updateStatus(request, httpRequest);}
    //编辑用户信息
    @PostMapping("/editUser")
    public Result<String> editUser(@RequestBody EditUserRequest request, HttpServletRequest httpRequest) { return userService.editUser(request, httpRequest); }

    //bookService
    //新增图书
    @PostMapping("/addBook")
    public Result<String> addBook(@RequestBody AddBookRequest request, HttpServletRequest httpRequest) {return bookService.addBook(request, httpRequest);}
    //编辑图书
    @PostMapping("/editBook")
    public Result<String> editBook(@RequestBody EditBookRequest request, HttpServletRequest httpRequest) {return bookService.editBook(request, httpRequest);}

}