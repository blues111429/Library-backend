package org.example.backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Integer user_id;
    private String username;
    private String password_hash;
    private String name;
    private String gender;
    private String type;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime last_login;
    private LocalDateTime status_update_time;
    private String type_cn;
}
