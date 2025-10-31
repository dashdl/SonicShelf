package com.zhongxin.sonicshelf.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlaylistsResponse {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String userName;
    private String musicCount;
    private String playCount;
    private LocalDateTime createTime;
    private String userAvatar;
}
