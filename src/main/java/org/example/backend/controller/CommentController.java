package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.request.comment.PublishCommentRequest;
import org.example.backend.dto.response.Result;
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

    @GetMapping("/getComments/{bookId}")
    public Result<List<Comment>> getAllComments(@PathVariable Integer bookId) {  return commentService.getAllComments(bookId); }

    @PostMapping("/publishComment")
    public Result<String> publishComment(@RequestBody PublishCommentRequest request, HttpServletRequest httpRequest) { return commentService.publishComment(request, httpRequest); }
}
