package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class AlbumInfoResponse {
    private Long id;
    private Long artistId;
    private String artistName;
    private String artistCover;
    private String title;
    private String coverImage;
    private String description;
    private int musicCount;
    private String releaseDate;
}
