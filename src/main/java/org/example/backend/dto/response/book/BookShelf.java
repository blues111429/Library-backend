package org.example.backend.dto.response.book;

import lombok.Data;

@Data
public class BookShelf {
    private Integer id;
    private String title;
    private String author;
    private String isbn;
    private Integer status;
    private Integer availableCopies;
    private String categoryName;
    private String coverUrl;
}
