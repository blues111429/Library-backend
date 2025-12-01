package org.example.backend.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.response.user.UserListResponse;
import org.example.backend.mapper.AdminMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserTools {
    //依赖注入
    private static UserMapper userMapper = null;
    private static AdminMapper adminMapper = null;
    private static JwtUtil jwtUtil;
    public UserTools(UserMapper userMapper, AdminMapper adminMapper, JwtUtil jwtUtil) {
        UserTools.userMapper = userMapper;
        UserTools.adminMapper = adminMapper;
        UserTools.jwtUtil = jwtUtil;
    }

    //获取token
    private static String getToken(HttpServletRequest httpRequest) { return httpRequest.getHeader("Authorization"); }
    //获取用户名
    private static String getUsername(String token) { return jwtUtil.getUsernameFromToken(token); }
    //获取用户
    private static User getUser(String username) { return userMapper.findByUsername(username); }

    //校验
    //登录检验
    public static String tokenCheck(HttpServletRequest httpRequest) {
        String token = getToken(httpRequest);
        if(token == null || !token.startsWith("Bearer ")) {return "未授权访问,请先登录";}

        token = token.substring(7);
        if(!jwtUtil.validateToken(token)) {return "Token无效,请重新登录";}
        //获取当前用户信息
        String username =getUsername(token);
        User user = getUser(username);
        if(user==null) {return "用户不存在,请重新登录";}
        System.out.println("当前用户:" + user);
        return "";
    }
    //管理员身份校验
    public static String adminCheck(HttpServletRequest httpRequest) {
        //获取token、用户名、用户
        String token = getToken(httpRequest).substring(7);
        String username = getUsername(token);
        User currentUser = getUser(username);

        System.out.println("当前用户：" + currentUser);
        if(currentUser == null) { return "当前用户不存在"; }
        //管理员身份
        if(!"管理员".equals(currentUser.getType_cn())) { return "权限不足，仅管理员可以访问"; }

        return "";
    }
    //检查是否为管理员(会直接判断是否登录)
    public static int isAdmin(HttpServletRequest httpRequest) {
        String adminMessage = adminCheck(httpRequest);
        if(adminMessage.isEmpty()) {
            return 1;
        }
        return 0;
    }
    //注册数据校验
    public static String registerCheck(RegisterRequest request) {
        //用户名重复校验
        User existing = userMapper.findByOnlyUsername(request.getPhone());
        if (existing != null) {
            return "该手机号已被注册";
        }
        //手机号校验
        if (request.getPhone() == null || !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            return "手机号格式不正确";
        }
        //邮箱校验
        if(request.getEmail()==null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "邮箱格式不对";
        }
        //密码
        if(request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return "密码不能为空";
        }
        return "";
    }

    //获取信息
    //从请求中获取用户ID
    public static Integer getUserIdFromRequest(HttpServletRequest httpRequest) {
        String message = tokenCheck(httpRequest);
        if(!message.isEmpty()) {
            throw new RuntimeException(message);
        }

        String token = getToken(httpRequest).substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("无效token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return user.getUser_id();
    }

    //配置
    //设置用户注册
    public static User userRegister(RegisterRequest request) {
        //密码加密
        String encryptedPassword = PasswordUtil.encrypt(request.getPassword());
        //创建新用户
        User user = User.builder()
                .username(request.getPhone())
                .password_hash(encryptedPassword)
                .name(request.getName())
                .gender(request.getGender())
                .type(request.getType())
                .type_cn(request.getType())
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(1)
                .build();
        System.out.println("注册用户信息"+user);
        return user;
    }
    //设置获取用户列表返回的response
    public static UserListResponse getUserListResponse(User user) {
        return UserListResponse.builder()
                .user_id(user.getUser_id())
                .username(user.getUsername())
                .gender(user.getGender())
                .type(user.getType())
                .typeCn(user.getType_cn())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .create_time(user.getCreate_time())
                .last_login(user.getLast_login())
                .status_update_time(user.getStatus_update_time())
                .build();
    }

    //数据插入
    //记录管理员操作
    public static void adminLog(HttpServletRequest httpRequest, String action) {
        if(isAdmin(httpRequest) != 0) {
            Integer adminId = getUserIdFromRequest(httpRequest);
            adminMapper.insertLog(adminId, action);
        }
    }
}
