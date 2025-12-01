package org.example.backend.controller;

import org.example.backend.dto.response.Result;
import org.example.backend.model.Comment;
import org.example.backend.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/getAllComments")
    public Result<List<Comment>> getAllComments() {  return commentService.getAllComments(); }
}
