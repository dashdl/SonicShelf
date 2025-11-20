package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class Categories {
    private Integer id;
    private Integer parentId;
    private String name;
    private String description;
}
