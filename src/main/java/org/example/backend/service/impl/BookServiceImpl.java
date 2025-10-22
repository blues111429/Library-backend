package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.mapper.AdminMapper;
import org.example.backend.mapper.BookMapper;
import org.example.backend.service.BookService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final AdminMapper adminMapper;
    public BookServiceImpl(BookMapper bookMapper, AdminMapper adminMapper) {
        this.bookMapper = bookMapper;
        this.adminMapper = adminMapper;
    }

    @Override
    public Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        //日志
        Integer adminId = UserTools.getUserIdFromRequest(httpRequest);
        String action = "获取所有图书信息";
        adminMapper.insertLog(adminId, action);

        List<BookListResponse> books = bookMapper.getAllBooks();
        return Result.success(books);
    }
}
