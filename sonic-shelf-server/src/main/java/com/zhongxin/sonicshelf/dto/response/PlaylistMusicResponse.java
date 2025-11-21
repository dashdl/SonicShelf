package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class PlaylistMusicResponse {
    private Long id;
    private String title;
    private String artistName;
    private String albumTitle;
    private String coverImage;
    private String duration;
}
