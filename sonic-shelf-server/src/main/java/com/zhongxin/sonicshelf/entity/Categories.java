package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class Categories {
    private Long id;
    private Long parentId;
    private String name;
    private String description;
}
