package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.backend.dto.request.book.AddBookRequest;
import org.example.backend.dto.request.book.EditBookRequest;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.dto.response.book.BookShelf;
import org.example.backend.model.Book;

import java.util.List;

@Mapper
public interface BookMapper {
    //获取全部图书
    List<BookListResponse> getAllBooks();
    //查询图书(ID)
    @Select("SELECT * FROM book WHERE id = #{id}")
    Book findBookById(@Param("id") Integer id);
    //(ISBN)
    @Select("SELECT * FROM book WHERE isbn = #{isbn}")
    Book findBookByIsbn(@Param("isbn") String isbn);
    //(Title)
    @Select("SELECT * FROM book WHERE title = #{title}")
    Book findBookByTitle(@Param("title") String title);
    //状态修改
    @Update("UPDATE `book` SET status = #{status} WHERE id = #{bookId}")
    int updateStatus(@Param("bookId") Integer bookId, @Param("status") Integer status);
    //增加浏览数
    @Update("UPDATE `book` SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Integer id);
    //编辑图书
    int editBook(EditBookRequest request);
    //新增图书
    int addBook(AddBookRequest request);
    //更新可借阅数量
    int updateAvailableCopies(@Param("id") Integer id, @Param("num") Integer num);
    //查询用户书架图书
    @Select("""
            SELECT b.id, b.title, b.author, b.status, b.isbn, b.available_copies AS availableCopies,
            c.name AS categoryName
            FROM borrow_record br
            JOIN book b ON br.book_id = b.id
            LEFT JOIN category c ON b.category_id = c.id
            WHERE br.user_id = #{userId} AND br.status = 'borrowed'
           """)
    List<BookShelf> selectBooksByUserId(@Param("userId") Integer userId);
}
