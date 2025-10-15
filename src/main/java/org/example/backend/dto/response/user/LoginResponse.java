package org.example.backend.dto.response.user;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private Integer userId;
    private String username;
    private String typeCn;
    private String token;
}
