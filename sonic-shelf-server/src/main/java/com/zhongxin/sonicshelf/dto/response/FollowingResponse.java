package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

@Data
public class FollowingResponse {
    private Long id;
    private Long userId;
    private String type;
    private String name;
    private String description;
    private String coverImage;
}
