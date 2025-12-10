package org.example.backend.dto.request.comment;

import lombok.Data;

@Data
public class GetCommentsRequest {
    private Integer user_id;
    private Integer book_id;
}
