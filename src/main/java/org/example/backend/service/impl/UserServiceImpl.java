package org.example.backend.service.impl;

import org.example.backend.dto.request.LoginRequest;
import org.example.backend.dto.request.RegisterRequest;
import org.example.backend.dto.response.LoginResponse;
import org.example.backend.dto.response.RegisterResponse;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.example.backend.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    //mapper注入(构造方法)
    private final UserMapper userMapper;
    public UserServiceImpl (UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    //登录
    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        User user = userMapper.findByUsername(request.getUsername());
        if( user == null ) {
            response.setMessage("用户名不存在");
            return response;
        }

        boolean match = PasswordUtil.matches(request.getPassword(), user.getPassword_hash());
        if(!match) {
            response.setMessage("密码错误");
            return response;
        }

        userMapper.updateLastLogin(user.getUser_id());

        response.setMessage("登录成功");
        response.setUserId(user.getUser_id());
        response.setUsername(user.getUsername());
        response.setType(user.getType());
        return response;
    }

    //注册
    @Override
    public RegisterResponse register(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            response.setMessage("用户名已存在");
            return response;
        }

        String encryptedPassword = PasswordUtil.encrypt(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword_hash(encryptedPassword);
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setType(request.getType());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);

        if(userMapper.insert(user) <= 0) {
            response.setMessage("注册失败");
            return response;
        }
        response.setMessage("注册成功");
        response.setUserId(user.getUser_id());
        return response;
    }
}
