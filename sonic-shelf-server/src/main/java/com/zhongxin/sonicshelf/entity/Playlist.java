package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class Playlist {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String userId;
    private String isPublic;
    private String musicCount;
    private String playCount;
    private String favoriteCount;
    private String shareCount;
    private String status;
    private String createTime;
    private String updateTime;
}
