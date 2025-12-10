package org.example.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.checkerframework.checker.units.qual.C;
import org.example.backend.model.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    //获取评论
    @Select("SELECT * FROM comments WHERE book_id = #{bookId}")
    List<Comment> comments(@Param("bookId") Integer bookId);

    //发布评论
    @Insert("INSERT INTO comments (user_id, book_id, comment_text, rating, created_at, updated_at, status)" +
            "VALUES (#{user_id}, #{book_id}, #{comment_text}, 0, NOW(), NOW(), 'active')")
    int publishComment(@Param("user_id") Integer user_id, @Param("book_id") Integer book_id, @Param("comment_text") String comment_text);

    //获取某人的全部评论
    @Select("SELECT * FROM comments WHERE user_id = #{user_id}")
    List<Comment> findCommentsByUserId(@Param("user_id") Integer user_id);
}
