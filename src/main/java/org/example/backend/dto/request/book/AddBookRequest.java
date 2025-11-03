package org.example.backend.dto.request.book;

import lombok.Data;

@Data
public class AddBookRequest {
    private String title;
    private String author;
    private String categoryId;
    private String isbn;
    private Integer totalCopies;
    private Integer availableCopies;
    private String publisher;
    private Integer publishYear;
}
