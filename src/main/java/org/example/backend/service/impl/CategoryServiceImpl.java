package org.example.backend.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.response.Result;
import org.example.backend.mapper.AdminMapper;
import org.example.backend.mapper.CategoryMapper;
import org.example.backend.model.Category;
import org.example.backend.service.CategoryService;
import org.example.backend.util.UserTools;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final AdminMapper adminMapper;
    public CategoryServiceImpl(CategoryMapper categoryMapper, AdminMapper adminMapper) {
        this.categoryMapper = categoryMapper;
        this.adminMapper = adminMapper;
    }

    @Override
    public Result<List<Category>> getAllCategories(HttpServletRequest httpRequest) {
        //管理员校验
        String message = UserTools.adminCheck(httpRequest);
        if(!message.isEmpty()) { return Result.error(message); }

        Integer adminId = UserTools.getUserIdFromRequest(httpRequest);
        String action = "获取分类列表";
        adminMapper.insertLog(adminId, action);

        List<Category> categoryList = categoryMapper.getAllCategories();
        return Result.success(categoryList);
    }
}
