package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.book.UpdateStatusRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.mapper.BookMapper;
import org.example.backend.model.Book;
import org.example.backend.service.BookService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;

    public BookServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    //用户/管理员
    //获取图书列表
    @Override
    public Result<List<BookListResponse>> getBookList(HttpServletRequest httpRequest) {
        List<BookListResponse> books = bookMapper.getAllBooks();
        UserTools.adminLog(httpRequest, "获取图书列表");
        return Result.success(books);
    }

    //管理员
    //修改图书状态
    @Override
    public Result<String> updateStatus(UpdateStatusRequest request, HttpServletRequest httpRequest) {
        //管理员身份校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }
        //查询图书
        Book book = bookMapper.findBookById(request.getId());
        //状态修改
        if(bookMapper.updateStatus(request.getId(), request.getStatus()) <= 0) {
            return Result.error("修改失败");
        }
        UserTools.adminLog(httpRequest, "修改图书状态, 图书ID:"+book.getId()+", 状态由"+book.getStatusText()+"修改至"+(request.getStatus() == 1 ? "上架" : "下架"));
        return Result.success("修改成功");
    }
}
