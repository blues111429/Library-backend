package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {
    //获取全部图书
    Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest);
}
