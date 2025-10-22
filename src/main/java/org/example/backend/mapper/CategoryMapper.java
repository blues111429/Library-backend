package org.example.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM category")
    List<Category> getAllCategories();
}
