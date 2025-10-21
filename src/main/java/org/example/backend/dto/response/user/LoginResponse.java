package org.example.backend.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String name;
    private Integer userId;
    private String username;
    private String typeCn;
    private String token;
}
