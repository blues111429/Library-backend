package org.example.backend.dto.request.user;

import lombok.Data;

@Data
public class UpdateUserInfoRequest {
    private Integer user_id;
    private String name;
    private String gender;
    private String phone;
    private String email;
}
