package org.example.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer user_id;
    private String username;
    private String password_hash;
    private String name;
    private String gender;
    private String type;
    private String type_cn;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime last_login;
    private LocalDateTime status_update_time;
}
