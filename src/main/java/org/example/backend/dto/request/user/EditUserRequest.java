package org.example.backend.dto.request.user;

import lombok.Data;

@Data
public class EditUserRequest {
    private Integer user_id;
    private String username;
    private String phone;
    private String name;
    private String gender;
    private String type;
    private String email;
}
