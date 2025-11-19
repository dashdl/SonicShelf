package com.zhongxin.sonicshelf.dto.request;

import lombok.Data;

@Data
public class MusicManageRequest {
    private Long id;
    private String title;
    private Long artistId;
    private Long albumId;
    private Integer duration;
    private String fileUrl;
    private Long[] categoryIds;
}
