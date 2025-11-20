package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class Category {
    private String id;
    private String name;

    public Category() {
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
