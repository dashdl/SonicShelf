package com.zhongxin.sonicshelf.dto.response;

import com.zhongxin.sonicshelf.entity.Category;
import lombok.Data;

import java.util.List;

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
    private List<Category> categories;
    private String createdAt;
    private String updatedAt;
}
