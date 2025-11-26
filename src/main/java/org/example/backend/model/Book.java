package org.example.backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Book {
    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String isbn;
    private Integer categoryId;
    private String language;
    private String description;
    private String coverUrl;
    private Integer totalCopies;
    private Integer availableCopies;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;// 1=上架 0=下架
    private Integer viewCount;       // 浏览次数
    private Integer borrowCount;    //借阅次数
    private String tags;

    public String getStatusText() {
        if(status == null) return "未知";
        return status == 1 ? "上架" : "下架";
    }

    public List<String> getTagsList() {
        if(tags == null || tags.isEmpty()) return new ArrayList<>();
        return Arrays.asList(tags.split(","));
    }
}
