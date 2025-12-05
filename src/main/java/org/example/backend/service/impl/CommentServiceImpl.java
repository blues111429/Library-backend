package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.comment.PublishCommentRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.model.Comment;
import org.example.backend.service.CommentService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public Result<List<Comment>> getAllComments(Integer bookId) {
        List<Comment> comments = commentMapper.comments(bookId);
        if(comments.isEmpty()) {
            return Result.error("该图书暂无评论");
        }
        return Result.success(comments);
    }

    @Override
    public Result<String> publishComment(PublishCommentRequest request, HttpServletRequest httpRequest) {
        int userId = UserTools.getUserIdFromRequest(httpRequest);
        String newComment = request.getNewComment();
        Integer bookId = request.getBook_id();
        if(newComment.isEmpty()) {return Result.error("请输入评论内容");}
        System.out.println("发布评论用户:"+userId+", 新评论:"+newComment+", 书的ID:"+bookId);
        return Result.success("发布成功");
    }
}
