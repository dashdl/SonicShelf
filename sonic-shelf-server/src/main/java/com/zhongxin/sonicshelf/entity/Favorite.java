package com.zhongxin.sonicshelf.entity;

import com.zhongxin.sonicshelf.util.CurrentUserUtil;
import lombok.Data;

import java.util.Date;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private String targetType;   // 目标类型："music", "album", "playlist"
    private Long targetId;
    private Date createTime;

    public Favorite(String targetType, Long targetId) {
        this.userId = CurrentUserUtil.getCurrentUserId();
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public Favorite() {
    }
}
