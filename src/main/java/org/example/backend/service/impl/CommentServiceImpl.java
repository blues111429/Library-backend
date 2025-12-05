package org.example.backend.service.impl;

import org.example.backend.dto.response.Result;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.model.Comment;
import org.example.backend.service.CommentService;
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
}
