package org.example.backend.service;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    LoginResponse login(LoginRequest request);
}
