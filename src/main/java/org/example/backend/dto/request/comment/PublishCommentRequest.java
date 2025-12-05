package org.example.backend.dto.request.comment;

import lombok.Data;

@Data
public class PublishCommentRequest {
    private Integer user_id;
    private Integer book_id;
    private String newComment;
}
