package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class MusicResponse {
    private String id;
    private String title;
    private String coverImage;
    private String artistName;
    private String albumTitle;
    private String fileUrl;
    private String duration;
    private String isFavorite;
}
