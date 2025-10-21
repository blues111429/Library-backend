package org.example.backend.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.user.EditUserRequest;
import org.example.backend.dto.request.user.RegisterRequest;
import org.example.backend.dto.response.user.UserListResponse;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserTools {
    //依赖注入
    private static UserMapper userMapper = null;
    private static JwtUtil jwtUtil;
    public UserTools(UserMapper userMapper, JwtUtil jwtUtil) {
        UserTools.userMapper = userMapper;
        UserTools.jwtUtil = jwtUtil;
    }

    //校验
    //未登录检验
    public static String tokenCheck(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        String message = "";
        if(token == null || !token.startsWith("Bearer ")) {
            message = "未授权访问，请先登录";
        }
        return message;
    }
    //管理员身份校验
    public static String adminCheck(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        //未登录
        if (token == null || !token.startsWith("Bearer ")) {
            return "未授权访问,请先登录";
        }
        //去掉‘Bearer’
        token = token.substring(7);
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return "Token已无效或已过期,请重新登陆";
        }
        //查找当前用户
        User currentUser = userMapper.findByUsername(username);
        System.out.println("当前用户：" + currentUser);
        if(currentUser == null) { return "当前用户不存在"; }

        if(!"管理员".equals(currentUser.getType_cn())) { return "权限不足，仅管理员可以访问"; }

        return "";
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
        return "";
    }

    //获取信息
    //从请求中获取用户ID
    public static Integer getUserIdFromRequest(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("缺少token");
        }

        token = token.substring(7);
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
    //新建用户，并编辑设置其字段的值
    public static User newUser(EditUserRequest request) {
        User user = new User();
        user.setUser_id(request.getUser_id());
        user.setUsername(request.getPhone());
        user.setPhone(request.getPhone());
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setType(request.getType());
        user.setType_cn(request.getType());
        user.setEmail(request.getEmail());
        return user;
    }
    //设置用户注册
    public static User userRegister(RegisterRequest request) {
        //密码加密
        String encryptedPassword = PasswordUtil.encrypt(request.getPassword());
        //创建新用户
        User user = new User();
        user.setUsername(request.getPhone());
        user.setPassword_hash(encryptedPassword);
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setType(request.getType());
        user.setType_cn(request.getType());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        System.out.println("注册用户信息"+user);
        return user;
    }
    //设置获取用户列表返回的response
    public static UserListResponse getUserListResponse(User user) {
        UserListResponse response = new UserListResponse();
        response.setUser_id(user.getUser_id());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setGender(user.getGender());
        response.setType(user.getType());
        response.setTypeCn(user.getType_cn());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setCreate_time(user.getCreate_time());
        response.setLast_login(user.getLast_login());
        response.setStatus_update_time(user.getStatus_update_time());
        return response;
    }
}
