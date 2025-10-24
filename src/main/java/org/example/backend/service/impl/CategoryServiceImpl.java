package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.mapper.CategoryMapper;
import org.example.backend.model.Category;
import org.example.backend.service.CategoryService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    //依赖注入
    private final CategoryMapper categoryMapper;
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    //获取分类列表
    @Override
    public Result<List<Category>> getAllCategories(HttpServletRequest httpRequest) {
        UserTools.adminLog(httpRequest, "获取所有图书信息");
        List<Category> categoryList = categoryMapper.getAllCategories();
        return Result.success(categoryList);
    }
}
