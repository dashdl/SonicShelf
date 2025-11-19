package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class AlbumManageResponse {
    private Long id;
    private String title;
    private String artistId;
    private String artistName;
    private String releaseDate;
    private String description;
    private String coverImage;
    private String musicCount;
    private String createAt;
    private String updateAt;
}
