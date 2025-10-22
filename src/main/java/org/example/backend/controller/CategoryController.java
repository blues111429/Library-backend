package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.model.Category;
import org.example.backend.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categoryList")
    public Result<List<Category>> getAllCategories(HttpServletRequest httpRequest) { return categoryService.getAllCategories(httpRequest); }
}
