package org.example.backend.dto.response.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserListResponse {
    private String message;
    private Integer user_id;
    private String username;
    private String name;
    private String gender;
    private String typeCn;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime last_login;
}
