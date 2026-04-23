package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.backend.dto.response.dashboard.CategoryCount;
import org.example.backend.dto.response.dashboard.DailyBorrowCount;

import java.util.List;

@Mapper
public interface DashBoardMapper {
    //总览数据
    @Select("SELECT COUNT(*) FROM borrow_record WHERE status='borrowed'")
    Integer getBorrowedBookCount();

    @Select("SELECT COUNT(*) FROM borrow_record WHERE status='borrowed' AND return_date < CURDATE()")
    Integer getOverDueBook();

    @Select("SELECT COUNT(*) FROM user WHERE status = 1")
    Integer getRegisterUser();

    @Select("SELECT COUNT(*) FROM book")
    Integer getTotalBook();

    //近30天借阅情况
    @Select("SELECT DATE(borrow_date) AS borrow_date, COUNT(*) AS borrow_count " +
            "FROM borrow_record WHERE borrow_date >= CURDATE() - INTERVAL 30 DAY " +
            "GROUP BY DATE(borrow_date) " +
            "ORDER BY borrow_date ASC")
    List<DailyBorrowCount> get30DaysBorrowCount();
    //图书分类情况
    @Select("SELECT c.name, COUNT(b.id) AS count FROM book b JOIN category c ON b.category_id = c.id GROUP BY category_id")
    List<CategoryCount> getCategoryCounts();
}