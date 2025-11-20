package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.Category;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistManageResponse {
    private Long id;
    private String title;
    private List<Category> categories;
    private String userNickname;
    private String description;
    private Integer musicCount;
    private Integer playCount;
    private Byte isPublic;
    private String coverImage;
    private String createdAt;
}
