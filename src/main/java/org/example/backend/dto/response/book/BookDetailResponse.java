package org.example.backend.dto.response.book;

import lombok.Data;

@Data
public class BookDetailResponse {
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String isbn;
    private String categoryName;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer status;
    private Integer viewCounts;
    private Integer borrowCount;
}
