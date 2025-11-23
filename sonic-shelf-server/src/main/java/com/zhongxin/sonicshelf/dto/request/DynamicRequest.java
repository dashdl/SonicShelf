package com.zhongxin.sonicshelf.dto.request;

import lombok.Data;

@Data
public class DynamicRequest {
    private Long id;
    private Long targetId;
    private String type;
    private String content;
}
