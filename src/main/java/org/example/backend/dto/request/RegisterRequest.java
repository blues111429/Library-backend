package org.example.backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String gender;
    private String type;
    private String phone;
    private String email;
}
