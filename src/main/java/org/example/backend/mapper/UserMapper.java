package org.example.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.backend.model.User;

@Mapper
public interface UserMapper {
    //根据用户名查找用户
    @Select("Select * FROM user Where username = #{username} ")
    User findByUsername(@Param("username") String username);

    //插入用户
    @Insert("INSERT INTO user (username, password_hash, name, gender, type, phone, email, status, create_time)" +
            "VALUES (#{username}, #{password_hash}, #{name}, #{gender}, #{type}, #{phone}, #{email}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "user_id")
    int insert(User user);

    //更新登录时间
    @Update("UPDATE user SET last_login = NOW() WHERE user_id = #{userId}")
    void updateLastLogin(@Param("userId") int userId);
}