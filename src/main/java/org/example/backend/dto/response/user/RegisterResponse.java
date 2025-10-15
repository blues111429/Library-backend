package org.example.backend.dto.response.user;

import lombok.Data;

@Data
public class RegisterResponse {
    private String message;
    private Integer userId;
    private String token;
}
