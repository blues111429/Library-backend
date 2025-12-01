package org.example.backend.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String message;
    private Integer userId;
}
