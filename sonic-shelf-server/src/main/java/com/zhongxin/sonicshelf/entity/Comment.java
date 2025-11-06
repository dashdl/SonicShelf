package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;
    private String createdAt;
    private Long parentId;
    private Long likeCount;
    private String targetType;
    private String targetId;
}
