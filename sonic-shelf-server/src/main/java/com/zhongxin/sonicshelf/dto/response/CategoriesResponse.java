package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class CategoriesResponse {
    private int id;
    private int parentId;
    private String name;
    private String description;
}
