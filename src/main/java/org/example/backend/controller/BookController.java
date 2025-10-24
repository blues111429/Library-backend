package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.book.UpdateStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //获取图书列表
    @GetMapping("/bookList")
    public Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest) {return bookService.getBookList(httpRequest);}
    //修改图书状态(上/下架)
    @PostMapping("/updateStatus")
    public Result<String> updateStatus(@RequestBody UpdateStatusRequest request, HttpServletRequest httpRequest) { return bookService.updateStatus(request, httpRequest); }
}
