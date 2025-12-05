package org.example.backend.service;

import org.example.backend.dto.response.Result;
import org.example.backend.model.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    Result<List<Comment>> getAllComments(Integer bookId);
}
