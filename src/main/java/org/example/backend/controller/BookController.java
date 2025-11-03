package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.book.BorrowBookRequest;
import org.example.backend.dto.request.book.ReturnBookRequest;
import org.example.backend.dto.request.book.UpdateStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.dto.response.book.BookShelf;
import org.example.backend.dto.response.book.BorrowRecordResponse;
import org.example.backend.model.Book;
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
    //图书详情页返回
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Integer id){return bookService.getBookById(id);}

    //图书借阅
    @PostMapping("/borrow")
    public Result<String> borrowBook(@RequestBody BorrowBookRequest request, HttpServletRequest httpRequest){ return bookService.borrowBook(request,httpRequest); }
    //归还图书
    @PostMapping("/return")
    public Result<String> returnBook(@RequestBody ReturnBookRequest request, HttpServletRequest httpRequest) { return bookService.returnBook(request,httpRequest); }
    //获取所有借阅记录
    @GetMapping("/borrowList")
    public Result<List<BorrowRecordResponse>> borrowRecord(HttpServletRequest httpRequest) { return bookService.borrowList(httpRequest);}
    //获取书架
    @GetMapping("/bookshelf")
    public Result<List<BookShelf>> getBookShelf(HttpServletRequest httpRequest) { return bookService.getBookShelf(httpRequest); }
}
