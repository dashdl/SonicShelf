package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class AlbumResponse {
    private int id;
    private String title;
    private String coverImage;
    private String description;
    private int musicCount;
    private String releaseDate;
}
