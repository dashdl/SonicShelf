package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class MusicInfoResponse {
    private String id;
    private String title;
    private String coverImage;
    private String artistName;
    private String isFavorite;
}
