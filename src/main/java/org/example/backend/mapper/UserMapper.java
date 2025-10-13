package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.User;

@Mapper
public interface UserMapper {
    @Select("Select id, username, password, role FROM user Where username = #{username} ")
    User findByUsername(@Param("username") String username);

}