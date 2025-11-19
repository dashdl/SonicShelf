package com.zhongxin.sonicshelf.dto.request;

import lombok.Data;

@Data
public class AlbumManageRequest {
    private Long id;
    private String title;
    private Long artistId;
    private String releaseDate;
    private String description;
}
