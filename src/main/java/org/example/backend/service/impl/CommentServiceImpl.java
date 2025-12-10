package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.comment.GetCommentsRequest;
import org.example.backend.dto.request.comment.PublishCommentRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.comment.ReturnCommentResponse;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.Comment;
import org.example.backend.model.User;
import org.example.backend.service.CommentService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    public CommentServiceImpl(UserMapper userMapper, CommentMapper commentMapper) {
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
    }

    //获取评论
    @Override
    public Result<List<ReturnCommentResponse>> getAllComments(GetCommentsRequest request) {
        int bookId = request.getBook_id();
        List<Comment> comments = commentMapper.comments(bookId);
        if(comments.isEmpty()) {
            return Result.error("该图书暂无评论");
        }
        List<ReturnCommentResponse> commentList = new ArrayList<>();
        for(Comment comment : comments) {
            ReturnCommentResponse response = ReturnCommentResponse.builder()
                    .name(userMapper.findUserById(comment.getUser_id()).getName())
                    .comment_text(comment.getComment_text())
                    .rating(comment.getRating())
                    .updated_at(comment.getUpdated_at())
                    .build();
            commentList.add(response);
        }
        return Result.success("获取成功", commentList);
    }
    //发布评论
    @Override
    public Result<String> publishComment(PublishCommentRequest request, HttpServletRequest httpRequest) {
        int userId = UserTools.getUserIdFromRequest(httpRequest);
        String newComment = request.getNewComment();
        Integer bookId = request.getBook_id();
        System.out.println("发布评论用户:"+userId+", 新评论:"+newComment+", 书的ID:"+bookId);
        if(commentMapper.publishComment(userId, bookId, newComment) <= 0) {return Result.error("发布失败");}
        return Result.success("发布成功");
    }
    //修改评论
    //删除评论
}
