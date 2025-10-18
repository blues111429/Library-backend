package org.example.backend.dto.request.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}
