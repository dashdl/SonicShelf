package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class DynamicResponse {
    private Long id;
    private Long userId;
    private Long targetId;
    private String type;
    private String avatar;
    private String userName;
    private String createdAt;
    private String content;
    private String[] images;
    private String coverImage;
    private String title;
    private String artistName;
    private String creatorName;
    private Integer likeCount;
    private Integer shareCount;
    private Integer commentCount;
    private boolean isLike;
}
