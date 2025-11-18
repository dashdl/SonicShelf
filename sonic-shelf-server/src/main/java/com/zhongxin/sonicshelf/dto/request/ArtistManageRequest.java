package com.zhongxin.sonicshelf.dto.request;

import lombok.Data;

@Data
public class ArtistManageRequest {
    private Long id;
    private String name;
    private Byte gender;
    private String country;
    private String description;
}
