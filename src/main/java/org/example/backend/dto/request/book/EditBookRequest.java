package org.example.backend.dto.request.book;

import lombok.Data;

@Data
public class EditBookRequest {
    private Integer id;
    private String title;
    private String author;
    private Integer categoryId;
    private String isbn;
    private Integer totalCopies;
    private Integer availableCopies;
    private String publisher;
    private Integer  publishYear;
}
