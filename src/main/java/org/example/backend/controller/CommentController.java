package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.comment.GetCommentsRequest;
import org.example.backend.dto.request.comment.PublishCommentRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.dto.response.comment.ReturnCommentResponse;
import org.example.backend.model.Comment;
import org.example.backend.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/getComments")
    public Result<List<ReturnCommentResponse>> getAllComments(@RequestBody GetCommentsRequest request) {  return commentService.getAllComments(request); }

    @PostMapping("/publishComment")
    public Result<String> publishComment(@RequestBody PublishCommentRequest request, HttpServletRequest httpRequest) { return commentService.publishComment(request, httpRequest); }
}
