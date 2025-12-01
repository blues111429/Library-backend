package org.example.backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Comment {
    private int comment_id;
    private int user_id;
    private int book_id;
    private String comment_text;
    private int rating;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String status;
}
