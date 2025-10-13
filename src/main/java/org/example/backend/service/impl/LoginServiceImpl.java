package org.example.backend.service.impl;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.LoginResponse;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;
    public LoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        LoginResponse loginResponse = new LoginResponse();

        if(user == null){
            loginResponse.setMessage("用户名不存在");
            return loginResponse;
        }

        if(!user.getPassword().equals(request.getPassword())){
            loginResponse.setMessage("密码错误");
        }

        loginResponse.setId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setRole(user.getRole());
        loginResponse.setMessage("登录成功");
        return loginResponse;
    }
}
