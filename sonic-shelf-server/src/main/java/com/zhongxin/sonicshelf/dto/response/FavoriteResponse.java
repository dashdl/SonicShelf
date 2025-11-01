package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class FavoriteResponse {
    private Long favoriteId;   // 收藏ID
    private Long targetId;     // 目标ID
    private String targetType; // 目标类型
    private Date createdAt;    // 创建时间
}
