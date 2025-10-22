package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.backend.dto.response.book.BookListResponse;
import java.util.List;

@Mapper
public interface BookMapper {

    //获取全部图书
    List<BookListResponse> getAllBooks();
}
