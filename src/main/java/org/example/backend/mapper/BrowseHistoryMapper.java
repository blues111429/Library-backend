package org.example.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.BrowseHistory;

import java.util.List;

@Mapper
public interface BrowseHistoryMapper {
    // 插入浏览记录
    @Insert("""
        INSERT INTO browse_history(user_id, book_id, browse_time)
        VALUES (#{userId}, #{bookId}, #{browseDate})
    """)
    void insert(BrowseHistory history);

    // 查询完整浏览记录
    @Select("""
        SELECT *
        FROM browse_history
        WHERE user_id = #{userId}
        ORDER BY browse_time DESC
    """)
    List<BrowseHistory> findByUserId(@Param("userId") Integer userId);

    // ✅ 新增：只返回浏览过的 book_id 列表
    @Select("""
        SELECT DISTINCT book_id
        FROM browse_history
        WHERE user_id = #{userId}
    """)
    List<Integer> findBookIdsByUser(@Param("userId") Integer userId);

    // 查询所有用户浏览记录（暂时保留）
    @Select("SELECT * FROM borrow_record")
    List<BrowseHistory> findBorrowHistory();

    // ✅ 新增：返回所有用户ID（供协同过滤使用）
    @Select("SELECT DISTINCT user_id FROM browse_history")
    List<Integer> findAllUserIds();
}
