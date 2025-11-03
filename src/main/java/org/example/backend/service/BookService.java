package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.book.*;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.dto.response.book.BookShelf;
import org.example.backend.dto.response.book.BorrowRecordResponse;
import org.example.backend.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {
    //获取全部图书
    Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest);
    //修改图书状态
    Result<String> updateStatus(UpdateStatusRequest request, HttpServletRequest httpRequest);
    //编辑图书
    Result<String> editBook(EditBookRequest request, HttpServletRequest httpRequest);
    //新增图书
    Result<String> addBook(AddBookRequest request, HttpServletRequest httpRequest);
    //获取图书详情
    Result<Book> getBookById(Integer id);
    //借阅图书
    Result<String> borrowBook(BorrowBookRequest request, HttpServletRequest httpRequest);
    //归还图书
    Result<String> returnBook(ReturnBookRequest request, HttpServletRequest httpRequest);
    //借阅列表
    Result<List<BorrowRecordResponse>> borrowList(HttpServletRequest httpRequest);
    //书架
    Result<List<BookShelf>> getBookShelf(HttpServletRequest httpRequest);
    //图书浏览记录
    Result<String> addHistory(BrowseHistoryRequest request, HttpServletRequest httpRequest);
}
