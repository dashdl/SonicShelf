package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.dto.request.ArtistManageRequest;
import lombok.Data;

@Data
public class ArtistManageResponse {
    private Long id;
    private String name;
    private String country;
    private Byte gender;
    private String description;
    private String coverImage;
    private Integer musicCount;
    private String createdAt;
}
