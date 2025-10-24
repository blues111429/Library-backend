package org.example.backend.dto.response.book;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookListResponse {
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String isbn;
    private Integer categoryId;
    private String categoryName;  // 前端直接显示分类名称
    private String language;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer viewCount;
    private Integer borrowCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
