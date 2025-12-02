package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class PlaylistBaseResponse {
    private Long id;
    private Long creatorId;
    private String title;
    private String creatorName;
    private String coverImage;
    private Integer musicCount;
    private Integer playCount;
}
