package org.example.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.backend.dto.response.book.BorrowRecordResponse;
import org.example.backend.model.BorrowRecord;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BorrowRecordMapper {
    //获取所有
    @Select("SELECT * FROM borrow_record")
    List<BorrowRecord> findAll();

    //根据借阅ID查询
    @Select("SELECT * FROM borrow_record WHERE id = #{id}")
    BorrowRecord selectById(@Param("id") Integer id);

    //按用户ID查询
    @Select("SELECT * FROM borrow_record WHERE user_id = #{user_id}")
    List<BorrowRecord> findByUserId(@Param("user_id") Integer user_id);

    //插入借阅记录
    @Insert("INSERT INTO borrow_record(user_id, book_id, borrow_date, due_date, status, create_time, update_time)" +
            "VALUES (#{userId}, #{bookId}, #{borrowDate}, #{dueDate}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BorrowRecord record);

    // 查询当前用户的借阅记录
    @Select("""
        SELECT br.id, b.title AS bookTitle, br.book_id AS bookId,
               br.borrow_date AS borrowDate, br.due_date AS dueDate,
               br.return_date AS returnDate, br.status
        FROM borrow_record br
        JOIN book b ON br.book_id = b.id
        WHERE br.user_id = #{userId}
        ORDER BY br.borrow_date DESC
    """)
    List<BorrowRecordResponse> selectAll(Integer userId);

    //判断用户是否已借阅(未归还)
    @Select("SELECT COUNT(1) FROM borrow_record " +
            "WHERE user_id = #{userId} AND book_id = #{bookId} AND return_date IS NULL")
    int countActiveBorrow(@Param("userId") Integer userId, @Param("bookId") Integer bookId);

    //更新借阅状态
    @Update("UPDATE borrow_record " +
            "SET status = 'returned', return_date = #{returnDate} " +
            "WHERE id = #{id} AND status = 'borrowed'")
    int updateReturned(@Param("id") Integer id, @Param("returnDate") LocalDateTime returnDate);
}
