package org.example.backend.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private Integer user_id;
    private String message;
    private String username;
    private String name;
    private String typeCn;
    private String gender;
    private String phone;
    private String email;
}
