package org.example.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.backend.model.BrowseHistory;

@Mapper
public interface BrowseHistoryMapper {
    //插入浏览记录
    @Insert("INSERT INTO browse_history(user_id, book_id, browse_time) " +
            "VALUES (#{userId}, #{bookId}, #{browseDate})")
    void insert(BrowseHistory history);
}
