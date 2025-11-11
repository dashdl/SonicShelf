package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class MusicResponse {
    private Long id;
    private String title;
    private String coverImage;
    private String artistName;
    private String albumTitle;
    private String albumId;
    private String fileUrl;
    private String duration;
    private boolean isFavorite;
}
