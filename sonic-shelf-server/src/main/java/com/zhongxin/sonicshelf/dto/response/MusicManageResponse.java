package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;

import java.util.ArrayList;
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
    private List<Category> categories = new ArrayList<>();
    private String createdAt;
    private String updatedAt;

    @Data
    public static class Category {
        private String id;
        private String name;

        // 可以添加构造方法方便创建
        public Category() {}
        public Category(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
