package org.example.backend.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String message;
    private String username;
    private String name;
    private String type;
    private String gender;
    private String phone;
    private String email;
}
