package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT * FROM comments ")
    List<Comment> comments();
}
