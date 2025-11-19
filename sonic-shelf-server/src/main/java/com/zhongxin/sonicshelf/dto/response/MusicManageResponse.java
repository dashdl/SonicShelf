package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class MusicManageResponse {
    private Long id;
    private String title;
    private Long artistId;
    private String artistName;
    private Long albumId;
    private String albumTitle;
    private Integer duration;
    private String fileUrl;
    private String coverImage;
    private Integer playCount;
    private String createdAt;
    private String updatedAt;
}
