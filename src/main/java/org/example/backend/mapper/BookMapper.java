package org.example.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.backend.dto.request.book.AddBookRequest;
import org.example.backend.dto.request.book.EditBookRequest;
import org.example.backend.dto.response.book.BookListResponse;
import org.example.backend.dto.response.book.BookShelf;
import org.example.backend.model.Book;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {
    @Select("SELECT * FROM book")
    List<Book> findAllBooks();

    // 获取全部图书
    List<BookListResponse> getAllBooks();

    // 查询图书(ID)
    @Select("SELECT * FROM book WHERE id = #{id}")
    Book findBookById(@Param("id") Integer id);

    @Select("SELECT b.*, GROUP_CONCAT(t.name) AS tags FROM book b " +
            "LEFT JOIN book_tag bt ON b.id = bt.book_id " +
            "LEFT JOIN tag t ON bt.tag_id = t.id " +
            "WHERE b.id = #{id} GROUP BY b.id")
    @Results({
            @Result(property = "tags", column = "tags")
    })
    Book findBookByIdWithTags(@Param("id") Integer id);

    // (ISBN)
    @Select("SELECT * FROM book WHERE isbn = #{isbn}")
    Book findBookByIsbn(@Param("isbn") String isbn);

    // (Title)
    @Select("SELECT * FROM book WHERE title = #{title}")
    Book findBookByTitle(@Param("title") String title);

    // 状态修改
    @Update("UPDATE `book` SET status = #{status} WHERE id = #{bookId}")
    int updateStatus(@Param("bookId") Integer bookId, @Param("status") Integer status);

    // 增加浏览数
    @Update("UPDATE `book` SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Integer id);

    // 编辑图书
    int editBook(EditBookRequest request);

    // 新增图书
    int addBook(AddBookRequest request);

    // 更新可借阅数量
    int updateAvailableCopies(@Param("id") Integer id, @Param("num") Integer num);

    // 查询用户书架图书
    @Select("""
            SELECT b.id, b.title, b.author, b.status, b.isbn, b.available_copies AS availableCopies,
                   c.name AS categoryName
            FROM borrow_record br
            JOIN book b ON br.book_id = b.id
            LEFT JOIN category c ON b.category_id = c.id
            WHERE br.user_id = #{userId} AND br.status = 'borrowed'
           """)
    List<BookShelf> selectBooksByUserId(@Param("userId") Integer userId);

    // ✅ 随机获取图书（返回 BookListResponse）
    @Select("""
        SELECT
            b.id, b.title, b.author, b.publisher,
            b.publish_year AS publishYear,
            b.isbn, b.category_id AS categoryId,
            c.name AS categoryName,
            b.language, b.total_copies AS totalCopies,
            b.available_copies AS availableCopies,
            b.view_count AS viewCount,
            b.borrow_count AS borrowCount,
            b.create_time AS createTime,
            b.update_time AS updateTime,
            b.status AS status
        FROM book b
        LEFT JOIN category c ON b.category_id = c.id
        ORDER BY RAND()
        LIMIT #{limit}
    """)
    List<BookListResponse> findRandomBooks(@Param("limit") int limit);

    // ✅ 单本图书详情（用于推荐结果中补全信息）
    @Select("""
        SELECT
            b.id, b.title, b.author, b.publisher,
            b.publish_year AS publishYear,
            b.isbn, b.category_id AS categoryId,
            c.name AS categoryName,
            b.language, b.total_copies AS totalCopies,
            b.available_copies AS availableCopies,
            b.view_count AS viewCount,
            b.borrow_count AS borrowCount,
            b.create_time AS createTime,
            b.update_time AS updateTime,
            b.status AS status
        FROM book b
        LEFT JOIN category c ON b.category_id = c.id
        WHERE b.id = #{id}
    """)
    BookListResponse findBookListById(@Param("id") Integer id);

    List<Map<String, Object>> countCategoryPreference(@Param("bookIds") List<Integer> bookIds);
    List<Map<String, Object>> countAuthorPreference(@Param("bookIds") List<Integer> bookIds);
}
