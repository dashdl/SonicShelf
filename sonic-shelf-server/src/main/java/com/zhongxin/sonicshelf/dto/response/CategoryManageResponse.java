package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class CategoryManageResponse {
    private Integer id;
    private Integer parentId;
    private String name;
    private String description;
    private Integer musicCount;
    private Integer playlistCount;
    private String createdAt;
}
