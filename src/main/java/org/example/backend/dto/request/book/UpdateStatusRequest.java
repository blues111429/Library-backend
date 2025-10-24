package org.example.backend.dto.request.book;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private Integer id;
    private Integer status;
}
