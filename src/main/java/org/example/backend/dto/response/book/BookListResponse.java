package org.example.backend.dto.response.book;

import lombok.Data;

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
}
