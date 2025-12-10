package org.example.backend.dto.response.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReturnCommentResponse {
    private String name;
    private String comment_text;
    private int rating;
    private LocalDateTime updated_at;
}
