package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class ArtistResponse {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private int albumCount;
    private String translatedName;
    private int followerCount;
}
