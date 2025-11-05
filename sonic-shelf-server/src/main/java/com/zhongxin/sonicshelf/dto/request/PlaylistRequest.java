package com.zhongxin.sonicshelf.dto.request;

import lombok.Data;

@Data
public class PlaylistRequest {
    private String title;
    private String description;
    private String isPublic;
    private Long[] tags;
}
