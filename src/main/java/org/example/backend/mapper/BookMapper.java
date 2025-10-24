package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.model.Book;

import java.util.List;

@Mapper
public interface BookMapper {
    //查询图书
    @Select("SELECT * FROM book WHERE id = #{bookId}")
    Book findBookById(@Param("bookId") Integer bookId);

    //状态修改
    @Update("UPDATE `book` SET status = #{status} WHERE id = #{bookId}")
    int updateStatus(@Param("bookId") Integer bookId, @Param("status") Integer status);

    //获取全部图书
    List<BookListResponse> getAllBooks();
}
