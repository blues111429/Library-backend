package org.example.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.AdminLog;

import java.util.List;

@Mapper
public interface AdminMapper {
    @Insert("INSERT INTO admin_log (admin_id, action) VALUES (#{adminId}, #{action})")
    void insertLog(@Param("adminId") Integer adminId, @Param("action") String action);

    @Select("SELECT * FROM admin_log ORDER BY create_time DESC")
    List<AdminLog> getAllLog();
}
