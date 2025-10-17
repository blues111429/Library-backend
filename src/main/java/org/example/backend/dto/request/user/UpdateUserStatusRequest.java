package org.example.backend.dto.request.user;

import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    private Integer userId;
    private Integer status;
}
