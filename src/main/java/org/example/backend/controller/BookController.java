package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/bookList")
    public Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest) {return bookService.getBookList(httpRequest);}
}
