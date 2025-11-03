package org.example.backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BrowseHistory {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private LocalDateTime browseDate;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
