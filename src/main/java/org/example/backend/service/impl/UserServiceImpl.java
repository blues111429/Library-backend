package org.example.backend.service.impl;

import org.example.backend.dto.RegisterRequest;
import org.example.backend.dto.RegisterResponse;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.example.backend.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    public UserServiceImpl (UserMapper userMapper) {
        this.userMapper = userMapper;
    }

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

        userMapper.insert(user);

        response.setMessage("注册成功");
        response.setUser_id(user.getUser_id());
        return response;
    }
}
