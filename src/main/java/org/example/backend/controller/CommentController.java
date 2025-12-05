package org.example.backend.controller;

import org.example.backend.dto.response.Result;
import org.example.backend.model.Comment;
import org.example.backend.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
