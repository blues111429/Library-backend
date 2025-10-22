package org.example.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.AdminLog;

import java.util.List;

@Mapper
//仅负责对管理员行为进行记录
public interface AdminMapper {
    @Insert("INSERT INTO admin_log (admin_id, action) VALUES (#{adminId}, #{action})")
    void insertLog(@Param("adminId") Integer adminId, @Param("action") String action);

    @Select("SELECT * FROM admin_log ORDER BY create_time DESC")
    List<AdminLog> getAllLog();
}
