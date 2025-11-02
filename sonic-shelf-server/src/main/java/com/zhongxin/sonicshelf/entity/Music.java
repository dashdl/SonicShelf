package com.zhongxin.sonicshelf.entity;

import lombok.Data;

@Data
public class Music {
    private Long id;
    private String title;
    private String artistId;
    private String albumId;
    private String duration;
    private String fileUrl;
    private String coverImage;
    private String playCount;
    private String lyrics;
}
