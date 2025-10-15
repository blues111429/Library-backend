package org.example.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.backend.model.User;
import java.util.List;

@Mapper
public interface UserMapper {
    //获取用户列表
    @Select("SELECT *, CASE type WHEN 'student' THEN '学生' WHEN 'teacher' THEN '教师' WHEN 'admin' THEN '管理员' END AS type_cn FROM user")
    List<User> userList();

    //根据用户名查找用户并检查用户状态
    @Select("Select *, CASE type WHEN 'student' THEN '学生' WHEN 'teacher' THEN '教师' WHEN 'admin' THEN '管理员' END AS type_cn FROM user Where username = #{username} AND status = 1")
    User findByUsername(@Param("username") String username);

    //插入用户
    @Insert("INSERT INTO user (username, password_hash, name, gender, type, phone, email, status, create_time)" +
            "VALUES (#{username}, #{password_hash}, #{name}, #{gender}, #{type}, #{phone}, #{email}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "user_id")
    int insert(User user);

    //删除用户
    @Delete("update user SET status = 0 WHERE user_id = #{userId}")
    int delete(@Param("userId") int userId);

    //更新登录时间
    @Update("UPDATE user SET last_login = NOW() WHERE user_id = #{userId}")
    void updateLastLogin(@Param("userId") int userId);

}